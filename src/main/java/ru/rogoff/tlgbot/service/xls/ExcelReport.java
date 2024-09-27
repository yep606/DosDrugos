package ru.rogoff.tlgbot.service.xls;

import java.util.List;

/**
 * Интерфейс, описывающий отчет excel
 */
public abstract class ExcelReport<T extends ExcelRowData> {

  /**
   * Возвращает список загогловков для excel файла
   */
  protected abstract List<String> getHeaders();

  /**
   * Получить данные
   */
  protected abstract List<T> rowsData();

  /**
   * Получить количество столбцов в строке
   */
  protected int getColumnCount() {
    return getHeaders().size();
  }
}
