package ru.rogoff.tlgbot.service.xls;

import java.util.List;

/**
 * Интерфейс, описывающий поведение строки данных в excel
 */
public interface ExcelRowData {

  /**
   * Получить значения ячеек для строки
   */
  List<String> getValues();
}
