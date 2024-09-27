package ru.rogoff.tlgbot.converter;

import org.springframework.stereotype.Component;
import ru.rogoff.tlgbot.model.User;
import ru.rogoff.tlgbot.model.xls.UserQuestionInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserQuestionConverter {

    public List<UserQuestionInfo> convert(Set<User> users) {
        return users.stream()
                .map(this::toUserQuestionInfo)
                .collect(Collectors.toList());
    }

    private UserQuestionInfo toUserQuestionInfo(User user) {
        return UserQuestionInfo.builder()
                .question(user.getQuestion())
                .notificationAllowed(user.isNotificationAllowed())
                .build();
    }
}
