package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static ru.rogoff.tlgbot.enums.Emoji.*;
import static ru.rogoff.tlgbot.enums.State.MAIN_MENU;
import static ru.rogoff.tlgbot.enums.State.SEND_VIDEO_LINK;
import static ru.rogoff.tlgbot.utils.MarkupGeneratorUtils.adminMarkup;

/**
 * Позволяет пользователям получать 'особые' статусы для взаимодействия с ботом, на которые он будет реагировать.
 * Посмотреть, как сделано в РЖД боте
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private static final InlineKeyboardMarkup BACKWARD_KEYBOARD = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Назад %s".formatted(LEFTWARDS_ARROW)).callbackData("admin_backward")
        );
    private static final String ADMIN_PANEL_MESSAGE = "<b>Админская панель</b>\nВидео: %s";

    private final VideoService videoService;
    private final UserStateService stateService;
    private final DocumentService docService;
    private final TelegramBot bot;

    public void sendReport(Long chatId) {
        SendResponse response = bot.execute(new SendMessage(chatId, "%s Генерируем отчет..".formatted(HOURGLASS_NOT_DONE)));
        Integer messageId = response.message().messageId();

        File file;
        try {
            file = docService.buildUserQuestionReport();
        } catch (IOException e) {
            log.error("Не удалось сформировать отчет");
            bot.execute(new EditMessageText(chatId, messageId, "Не удалось сформировать отчет"));
            return;
        }

        bot.execute(new SendDocument(chatId, file));
        bot.execute(new DeleteMessage(chatId, messageId));

        docService.deleteFile(file);
    }

    public void uploadLink(Long telegramUserId, Long chatId, Integer messageId) {
        if (hasVideoUploaded()) {
            bot.execute(
                    new EditMessageText(chatId, messageId, "Видео уже было загружено%s".formatted(EXCLAMATION_MARK))
                            .replyMarkup(BACKWARD_KEYBOARD)
            );
        }
        else {
            stateService.updateState(telegramUserId, SEND_VIDEO_LINK);
            bot.execute(
                    new EditMessageText(chatId, messageId, "Отправьте ссылку на видео")
                            .replyMarkup(BACKWARD_KEYBOARD)
            );
        }
    }

    public void moveBackward(Long telegramUserId, Long chatId, Integer messageId) {
        stateService.updateState(telegramUserId, MAIN_MENU);
        bot.execute(new EditMessageText(chatId, messageId, ADMIN_PANEL_MESSAGE.formatted(videoService.findVideo().getUrl()))
                .replyMarkup(adminMarkup())
                .parseMode(HTML));
    }

    private boolean hasVideoUploaded() {
        return StringUtils.isNotEmpty(videoService.findVideo().getUrl());
    }
}
