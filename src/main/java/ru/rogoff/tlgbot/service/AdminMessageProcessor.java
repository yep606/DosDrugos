package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMessageProcessor {

    private final TelegramBot bot;
    private final MessageGeneratorService messageGeneratorService;
    public void processMessage(Message message) {
        if ("/admin".equals(message.text())) {
            bot.execute(messageGeneratorService.adminPanelMessage(message.chat().id()));
        }
    }
}
