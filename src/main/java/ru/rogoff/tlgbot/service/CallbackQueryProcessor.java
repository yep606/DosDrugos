package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.enums.State;
import ru.rogoff.tlgbot.model.User;

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

        if (data.contains("admin_")) {
            if (data.contains("downloadExcel")) {
                adminService.sendReport(chatId);
                bot.execute(new AnswerCallbackQuery(callbackId));
            }
        } else {
            User user = userService.findByTelegramUserId(callback.from().id());

            boolean allowNotification = Boolean.parseBoolean(data);
            user.setNotificationAllowed(allowNotification);
            user.setState(State.ENDING);
            userService.save(user);

            log.info("Process callback query from {}, allow notification: {}", callback.from().username(), allowNotification);

            bot.execute(new DeleteMessage(chatId, messageId));
            bot.execute(new AnswerCallbackQuery(callbackId).showAlert(true).text("Благодарим за ответ"));
        }
    }
}
