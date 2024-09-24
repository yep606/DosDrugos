package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.converter.UserConverter;
import ru.rogoff.tlgbot.enums.State;
import ru.rogoff.tlgbot.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 1) Admin / User ROLES
 * 2) State machine
 * 3) Excel generation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProcessor {

    private static final String GREETING_MESSAGE =
            """
                    %s, приветствуем Вас!
                    Добро пожаловать на канал Dos Drugos.
                    Здесь Вы можете анонимно оставить Ваш вопрос для передачи «Разговоры с утками».
                    Пожалуйста, не стесняйтесь выражать Ваши мысли, эмоции, постарайтесь описать ситуацию как можно подробнее и ярче,
                    чтобы мы могли полноценно оценить ее и дать исчерпывающий ответ на Ваш вопрос.
                    """;

    private static final String ALLOW_NOTIFICATION_MESSAGE =
            """
                    %s, cпасибо за участие и оставленный вопрос!
                    Нажмите кнопку «да», если хотите получить уведомление о выходе выпуска шоу с Вашим вопросом.
                    В обратном случае нажмите кнопку «нет».
                    """;
    private static final List<Long> ADMIN_IDS = Arrays.asList(259891990L, 255602825L);

    private final TelegramBot bot;
    private final UserConverter converter;
    private final UserService userService;
    private final CallbackQueryProcessor callbackProcessor;
    private final AdminMessageProcessor adminMessageProcessor;

    public void processUpdate(Update update) {
        log.info("Update incoming: {}", update);
        if (hasCallbackQuery(update)) {
            callbackProcessor.process(update.callbackQuery());
        } else if (isAdmin(update.message().from().id()) && "/admin".equals(update.message().text())){
            adminMessageProcessor.processMessage(update.message());
        } else{
            Message userMessage = update.message();
            Long telegramUserId = userMessage.from().id();
            User user = userService.findByTelegramUserId(telegramUserId);

            SendMessage sendMessage = switch (user.getState()) {
                case GREETING -> greeting(userMessage);
                case REWRITE_QUESTION -> rewriteQuestion(userMessage);
                case ASK_QUESTION -> askQuestion(userMessage, user);
                case ASK_NOTIFICATION -> askNotification(userMessage);
                case ENDING -> ending(userMessage, user);
            };

            SendResponse response = bot.execute(sendMessage);

            log.info("Response send: {}", response.message());
        }
    }

    private SendMessage greeting(Message message) {
        if (message.text().equals("Задать вопрос")) {
            User user = converter.toEntity(message.from());
            user.setState(State.ASK_QUESTION);
            userService.save(user);

            return new SendMessage(message.chat().id(), "Напишите свой вопрос в чат!");
        }

        Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup("Задать вопрос")
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);
        return new SendMessage(message.chat().id(), GREETING_MESSAGE.formatted(message.from().firstName())).replyMarkup(replyKeyboardMarkup);
    }

    private SendMessage askQuestion(Message message, User user) {
        String question = message.text();
        user.setQuestion(question);
        user.setState(State.ASK_NOTIFICATION);
        userService.save(user);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Да").callbackData("true"),
                new InlineKeyboardButton("Нет").callbackData("false")
        );

        return new SendMessage(message.chat().id(), ALLOW_NOTIFICATION_MESSAGE.formatted(message.from().firstName()))
                .replyMarkup(inlineKeyboard);
    }

    private SendMessage askNotification(Message message) {
        return new SendMessage(message.chat().id(), "Нажмите да/нет под сообщением!");

    }

    private SendMessage ending(Message message, User user) {
        if (user.isNotificationAllowed()) {
            return new SendMessage(message.chat().id(), "Вы уже задали свой вопрос и получите уведомление о выпуске шоу!");

        } else {
            return new SendMessage(message.chat().id(), "Вы уже задали свой вопрос.");
        }
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
