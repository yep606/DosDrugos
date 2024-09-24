package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ru.rogoff.tlgbot.enums.Emoji.DOCUMENT;
import static ru.rogoff.tlgbot.enums.Emoji.MOVIE_CAMERA;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMessageProcessor {

    private static final String DOWNLOAD_EXCEL = "Скачать вопросы %s".formatted(DOCUMENT);
    private static final String SEND_LINK = "Отправить ссылку на видео %s".formatted(MOVIE_CAMERA);

    private final TelegramBot bot;
    public void processMessage(Message message) {
        if ("/admin".equals(message.text())) {
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
            inlineKeyboard.addRow(new InlineKeyboardButton(DOWNLOAD_EXCEL).callbackData("admin_downloadExcel"));
            inlineKeyboard.addRow(new InlineKeyboardButton(SEND_LINK).callbackData("admin_sendLink"));

            bot.execute(new SendMessage(message.chat().id(), "Админская панель")
                    .replyMarkup(inlineKeyboard));
        }
    }
}
