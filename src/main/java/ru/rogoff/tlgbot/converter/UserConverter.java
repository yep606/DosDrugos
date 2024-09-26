package ru.rogoff.tlgbot.converter;

import org.springframework.stereotype.Component;
import ru.rogoff.tlgbot.model.User;

@Component
public class UserConverter {

    public User toEntity(com.pengrad.telegrambot.model.User telegramUser, Long chatId) {
        return User.builder()
                .telegramUserId(telegramUser.id())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .username(telegramUser.username())
                .chatId(chatId)
                .build();
    }
}
