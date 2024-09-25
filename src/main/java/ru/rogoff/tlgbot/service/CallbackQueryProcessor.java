package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.model.User;

import static ru.rogoff.tlgbot.enums.State.MAIN_MENU;


@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackQueryProcessor {

    private final TelegramBot bot;
    private final UserService userService;
    private final AdminService adminService;

    public void process(CallbackQuery callback) {
        String callbackId = callback.id();
        String data = callback.data();
        Long chatId = callback.maybeInaccessibleMessage().chat().id();
        Integer messageId = callback.maybeInaccessibleMessage().messageId();
        Long callbackFrom = callback.from().id();

        if (data.contains("admin_")) { // admin
            if (data.contains("downloadExcel")) {
                adminService.sendReport(chatId);
            }
            if (data.contains("sendLink")) {
                adminService.uploadLink(callbackFrom, chatId, messageId);
            }
            if (data.contains("backward")) {
                adminService.moveBackward(callbackFrom, chatId, messageId);
            }
            bot.execute(new AnswerCallbackQuery(callbackId));
        } else { // user
            User user = userService.findByTelegramUserId(callbackFrom);

            boolean allowNotification = Boolean.parseBoolean(data);
            user.setNotificationAllowed(allowNotification);
            user.setState(MAIN_MENU);
            userService.save(user);

            log.info("Process callback query from {}, allow notification: {}", callback.from().username(), allowNotification);

            bot.execute(new DeleteMessage(chatId, messageId));
            bot.execute(new AnswerCallbackQuery(callbackId).showAlert(true).text("Благодарим за ответ"));
        }
    }
}
