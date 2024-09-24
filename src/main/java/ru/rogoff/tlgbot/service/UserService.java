package ru.rogoff.tlgbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.enums.State;
import ru.rogoff.tlgbot.model.User;
import ru.rogoff.tlgbot.repo.UserRepo;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User findByTelegramUserId(Long telegramUserId) {
        return userRepo.findByTelegramUserId(telegramUserId)
                .orElse(User.builder().state(State.GREETING).build());
    }

    public Set<User> findByTelegramUsersIds(Set<Long> telegramUsersIds) {
        return userRepo.findByTelegramUserIds(telegramUsersIds);
    }

    public Set<Long> findAllTelegramUsersIds() {
        return userRepo.findAllTelegramUserIds();
    }

    public void save(User user) {
        userRepo.save(user);
    }
}
