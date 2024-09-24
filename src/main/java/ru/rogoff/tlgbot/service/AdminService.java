package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static ru.rogoff.tlgbot.enums.Emoji.HOURGLASS_NOT_DONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

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
}
