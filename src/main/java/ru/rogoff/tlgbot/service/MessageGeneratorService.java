package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static ru.rogoff.tlgbot.utils.MarkupGeneratorUtils.adminMarkup;

@Service
@RequiredArgsConstructor
public class MessageGeneratorService {
    private final VideoService videoService;
    private static final String ADMIN_PANEL_MESSAGE = "<b>Админская панель</b>\nВидео: %s";

    // Добавить кэш на получение видео
    public SendMessage adminPanelMessage(Long chatId) {
        return new SendMessage(chatId, ADMIN_PANEL_MESSAGE.formatted(videoService.findVideo().getUrl()))
                .replyMarkup(adminMarkup())
                .parseMode(HTML);
    }
}
