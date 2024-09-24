package ru.rogoff.tlgbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rogoff.tlgbot.model.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.telegram_user_id FROM users u", nativeQuery = true)
    Set<Long> findAllTelegramUserIds();
    Optional<User> findByTelegramUserId(Long telegramUserId);

    @Query(value = "SELECT * FROM users u WHERE u.telegram_user_id IN (:telegramUsersIds)", nativeQuery = true)
    Set<User> findByTelegramUserIds(Set<Long> telegramUsersIds);
}
