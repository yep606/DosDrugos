package ru.rogoff.tlgbot.service.xls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelReportBuilder<T extends ExcelReport<? extends ExcelRowData>> {
    private static final String EXCEL_DIR = "generated-files/";

    public File buildAsFile(T data, String fileName) throws IOException {
        Workbook workbook = build(data);
        return writeToFile(workbook, fileName);
    }

    private File writeToFile(Workbook workbook, String fileName) throws IOException {
        Path directoryPath = Paths.get(EXCEL_DIR);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        File file = new File(EXCEL_DIR + fileName);
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            workbook.close();
        }
        return file;
    }

    private Workbook build(T report) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");
        fillSheet(report, sheet);
        return workbook;
    }

    private void fillSheet(T report, Sheet sheet) {
        fillHeaders(report, sheet);
        Set<? extends ExcelRowData> rowsData = report.rowsData();

        if (rowsData.isEmpty()) {
            return;
        }

        AtomicInteger rowCounter = new AtomicInteger(1);
        rowsData.forEach(rowData -> {
            Row row = sheet.createRow(rowCounter.getAndIncrement());
            fillRow(rowData.getValues(), row);
        });
        IntStream.range(0, report.getColumnCount())
                .forEach(sheet::autoSizeColumn);
    }

    private void fillHeaders(T report, Sheet sheet) {
        Row headersRow = sheet.createRow(0);
        List<String> headersValues = report.getHeaders();
        fillRow(headersValues, headersRow);
    }

    private void fillRow(List<String> valuesStream, Row row) {
        AtomicInteger columnCounter = new AtomicInteger(0);
        valuesStream.forEach(value -> {
            Cell cell = row.createCell(columnCounter.getAndIncrement());
            cell.setCellValue(value);
        });
    }
}
