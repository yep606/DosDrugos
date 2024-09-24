package ru.rogoff.tlgbot.service.xls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.rogoff.tlgbot.converter.UserQuestionConverter;
import ru.rogoff.tlgbot.model.User;
import ru.rogoff.tlgbot.model.xls.UserQuestionInfo;
import ru.rogoff.tlgbot.service.UserService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class QuestionsDataPreparer {

    private final UserService userService;
    private final UserQuestionConverter converter;

    public Set<UserQuestionInfo> getData(Set<Long> telegramUsersIds) {
        Set<User> users = userService.findByTelegramUsersIds(telegramUsersIds);
        return converter.convert(users);
    }

}
