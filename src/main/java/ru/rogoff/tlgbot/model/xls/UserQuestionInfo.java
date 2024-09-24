package ru.rogoff.tlgbot.model.xls;

import lombok.Builder;
import lombok.Value;
import ru.rogoff.tlgbot.service.xls.ExcelRowData;

import java.util.Arrays;
import java.util.List;

@Value
@Builder
public class UserQuestionInfo implements ExcelRowData {

    String question;
    boolean notificationAllowed;

    @Override
    public List<String> getValues() {
        return Arrays.asList(question, toStringNotificationAllowed(notificationAllowed));
    }

    private String toStringNotificationAllowed(boolean notificationAllowed) {
        return notificationAllowed ? "Да" : "Нет";
    }
}
