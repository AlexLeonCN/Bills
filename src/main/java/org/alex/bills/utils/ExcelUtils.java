package org.alex.bills.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class ExcelUtils {
    private ExcelUtils() {
    }

    public static List<List<String>> sheetToList(Sheet sheet) {
        if (sheet == null) {
            return List.of();
        }
        List<List<String>> result = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        int lastRowNum = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                result.add(new ArrayList<>());
                continue;
            }
            int lastCellNum = Math.max(row.getLastCellNum(), 0);
            List<String> values = new ArrayList<>(lastCellNum);
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String value = cell == null ? "" : formatter.formatCellValue(cell);
                values.add(value);
            }
            result.add(values);
        }
        return result;
    }
}
