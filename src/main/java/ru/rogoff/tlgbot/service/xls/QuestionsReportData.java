package ru.rogoff.tlgbot.service.xls;

import lombok.RequiredArgsConstructor;
import ru.rogoff.tlgbot.model.xls.UserQuestionInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class QuestionsReportData extends ExcelReport<UserQuestionInfo> {

    private static final List<String> HEADERS = Arrays.asList("Вопрос", "Уведомление");

    private final Set<UserQuestionInfo> rowsData;
    @Override
    protected List<String> getHeaders() {
        return HEADERS;
    }

    @Override
    protected Set<UserQuestionInfo> rowsData() {
        return rowsData;
    }
}
