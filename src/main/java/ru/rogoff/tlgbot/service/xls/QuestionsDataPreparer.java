package ru.rogoff.tlgbot.service.xls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.rogoff.tlgbot.converter.UserQuestionConverter;
import ru.rogoff.tlgbot.model.User;
import ru.rogoff.tlgbot.model.xls.UserQuestionInfo;
import ru.rogoff.tlgbot.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionsDataPreparer {

    private final UserService userService;
    private final UserQuestionConverter converter;

    // TODO: вынести отдельно вопросы пользователей
    public List<UserQuestionInfo> getData(Set<Long> telegramUsersIds) {
        Set<User> users = userService.findByTelegramUsersIds(telegramUsersIds)
                .stream().filter(u -> StringUtils.isNotEmpty(u.getQuestion()))
                .collect(Collectors.toSet());
        return converter.convert(users);
    }

}
