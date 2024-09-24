package ru.rogoff.tlgbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.model.xls.UserQuestionInfo;
import ru.rogoff.tlgbot.service.xls.ExcelReportBuilder;
import ru.rogoff.tlgbot.service.xls.QuestionsDataPreparer;
import ru.rogoff.tlgbot.service.xls.QuestionsReportData;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

/**
 * Работа с документами на экспорт
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final UserService userService;
    private final ExcelReportBuilder<QuestionsReportData> builder;
    private final QuestionsDataPreparer preparer;

    public File buildUserQuestionReport() throws IOException {
        Set<Long> telegramUsersIds = userService.findAllTelegramUsersIds();
        Set<UserQuestionInfo> questionsInfo = preparer.getData(telegramUsersIds);
        String reportName = "Вопросы от %s.xlsx".formatted(LocalDate.now());

        return builder.buildAsFile(new QuestionsReportData(questionsInfo), reportName);
    }

    public void deleteFile(File file) {
        boolean isDeleted = file.delete();
        if (!isDeleted) {
            log.warn(
                    "Failed to delete the temporary file: {}, path: {}",
                    file.getName(),
                    file.getPath()
            );
        }
    }

}
