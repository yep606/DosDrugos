package ru.rogoff.tlgbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.enums.State;
import ru.rogoff.tlgbot.repo.UserRepo;

/**
 * TODO: вынести статус в отдельную таблицу
 */
@Service
@RequiredArgsConstructor
public class UserStateService {

    private final UserRepo repo;

    public void updateState(Long telegramUserId, State state) {
        repo.updateStateByTelegramUserId(telegramUserId, state.name());
    }

}
