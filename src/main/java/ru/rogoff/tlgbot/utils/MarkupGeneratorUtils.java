package ru.rogoff.tlgbot.utils;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.experimental.UtilityClass;

import static ru.rogoff.tlgbot.enums.Emoji.*;

// TODO: статик методы просто возвращают объекты, вынести
@UtilityClass
public class MarkupGeneratorUtils {

    private static final String DOWNLOAD_EXCEL = "Скачать вопросы %s".formatted(DOCUMENT);
    private static final String SEND_LINK = "Отправить ссылку на видео %s".formatted(MOVIE_CAMERA);

    public static InlineKeyboardMarkup adminMarkup() {
        return new InlineKeyboardMarkup()
                .addRow(new InlineKeyboardButton(DOWNLOAD_EXCEL).callbackData("admin_downloadExcel"))
                .addRow(new InlineKeyboardButton(SEND_LINK).callbackData("admin_sendLink"));
    }
}
