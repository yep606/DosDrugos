package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.converter.UserConverter;
import ru.rogoff.tlgbot.enums.State;
import ru.rogoff.tlgbot.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static ru.rogoff.tlgbot.enums.Emoji.LIGHT_BULB;
import static ru.rogoff.tlgbot.enums.Emoji.STAR;
import static ru.rogoff.tlgbot.enums.State.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProcessor {

    private static final String GREETING_MESSAGE =
            """
                    Приветствуем Вас!
                    Добро пожаловать на канал Dos Drugos.
                    Здесь Вы можете анонимно оставить Ваш вопрос для передачи «Разговоры с утками».
                    Пожалуйста, не стесняйтесь выражать Ваши мысли, эмоции, постарайтесь описать ситуацию как можно подробнее и ярче,
                    чтобы мы могли полноценно оценить ее и дать исчерпывающий ответ на Ваш вопрос.
                    """;

    private static final String ALLOW_NOTIFICATION_MESSAGE =
            """
                    Спасибо за участие и оставленный вопрос!
                    Нажмите кнопку «да», если хотите получить уведомление о выходе выпуска шоу с Вашим вопросом.
                    В обратном случае нажмите кнопку «нет».
                    """;
    private static final List<Long> ADMIN_IDS = Arrays.asList(259891990L, 255602825L);

    private static final String ASK_QUESTION_TEXT = "Задать вопрос%s".formatted(LIGHT_BULB);
    private static final ReplyKeyboardMarkup ASK_QUESTION_MARKUP = new ReplyKeyboardMarkup(ASK_QUESTION_TEXT).oneTimeKeyboard(false).resizeKeyboard(true);

    private static final String SUCCESS_NOTIFICATION_SEND = "Уведомление успешно отправлено%s\nПолучили: <b>%d</b>";
    private final TelegramBot bot;
    private final UserConverter converter;
    private final UserService userService;
    private final UserStateService stateService;
    private final CallbackQueryProcessor callbackProcessor;
    private final AdminMessageProcessor adminMessageProcessor;
    private final NotificationService notificationService;

    public void processUpdate(Update update) {
        log.info("Update incoming: {}", update);
        if (hasCallbackQuery(update)) {
            callbackProcessor.process(update.callbackQuery());
        } else if (Objects.isNull(update.message())) { // BUG
            log.error("Telegram bug catch: " + update.callbackQuery());
        } else if (isAdmin(update.message().from().id()) && "/admin".equals(update.message().text())) {
            adminMessageProcessor.processMessage(update.message());
        } else {
            Message userMessage = update.message();
            Long telegramUserId = userMessage.from().id();
            User user = userService.findByTelegramUserId(telegramUserId);

            SendMessage sendMessage = switch (user.getState()) {
                case GREETING -> greeting(userMessage);
                case MAIN_MENU -> mainMenu(userMessage, user);
                case REWRITE_QUESTION -> rewriteQuestion(userMessage);
                case ASK_QUESTION -> askQuestion(userMessage, user);
                case ASK_NOTIFICATION -> askNotification(userMessage);
                // admin
                case SEND_VIDEO_LINK -> sendVideoLink(userMessage, user);
                default -> new SendMessage("default", "default");
            };

            SendResponse response = bot.execute(sendMessage);

            log.info("Response send: {}", response.message());
        }
    }

    // State GREETING получаем только один раз: когда пользователя в бд еще нет
    private SendMessage greeting(Message message) {
        User user = converter.toEntity(message.from(), message.chat().id());
        user.setState(State.MAIN_MENU);
        userService.save(user);

        return new SendMessage(message.chat().id(), GREETING_MESSAGE).replyMarkup(ASK_QUESTION_MARKUP);
    }

    private SendMessage mainMenu(Message message, User user) {
        if (message.text().equals(ASK_QUESTION_TEXT)) {
            if (StringUtils.isNotEmpty(user.getQuestion())) {
                return new SendMessage(message.chat().id(), "Вы уже задали свой вопрос!");
            }
            stateService.updateState(user.getTelegramUserId(), ASK_QUESTION);
            return new SendMessage(message.chat().id(), "Напишите свой вопрос в чат!");
        }
        return new SendMessage(message.chat().id(), "Не понимаю вас :(").replyMarkup(ASK_QUESTION_MARKUP);
    }

    private SendMessage askQuestion(Message message, User user) {
        String question = message.text();
        user.setQuestion(question);
        user.setState(ASK_NOTIFICATION);
        userService.save(user);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Да").callbackData("true"),
                new InlineKeyboardButton("Нет").callbackData("false")
        );

        return new SendMessage(message.chat().id(), ALLOW_NOTIFICATION_MESSAGE)
                .replyMarkup(inlineKeyboard);
    }

    private SendMessage askNotification(Message message) {
        return new SendMessage(message.chat().id(), "Нажмите да/нет под сообщением!");

    }

    // TODO: реализовать отправку по подписчикам
    // TODO: в транзакцию поменять стейт и сохранение видео
    // TODO: чек на валидную ссылку
    private SendMessage sendVideoLink(Message message, User user) {
        int notified = notificationService.notifyAll(message.text());
        stateService.updateState(user.getTelegramUserId(), MAIN_MENU);

        return new SendMessage(message.chat().id(), SUCCESS_NOTIFICATION_SEND.formatted(STAR, notified)).parseMode(HTML);
    }

    private SendMessage rewriteQuestion(Message message) {
        throw new UnsupportedOperationException();
    }

    private boolean hasCallbackQuery(Update update) {
        return Objects.nonNull(update.callbackQuery());
    }

    private static boolean isAdmin(Long userId) {
        return ADMIN_IDS.contains(userId);
    }

}
