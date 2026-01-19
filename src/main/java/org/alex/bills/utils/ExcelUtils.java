package org.alex.bills.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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

    public static List<List<String>> csvToList(File file) {
        if (file == null) {
            return List.of();
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            return csvToList(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<List<String>> csvToList(InputStream inputStream) {
        if (inputStream == null) {
            return List.of();
        }
        List<List<String>> result = new ArrayList<>();
        try (PushbackReader reader = new PushbackReader(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)), 1)) {
            List<String> row = new ArrayList<>();
            StringBuilder value = new StringBuilder();
            boolean inQuotes = false;
            boolean lastCharWasDelimiter = false;
            boolean sawAnyChar = false;
            int ch;
            while ((ch = reader.read()) != -1) {
                sawAnyChar = true;
                char c = (char) ch;
                if (inQuotes) {
                    if (c == '"') {
                        int next = reader.read();
                        if (next == '"') {
                            value.append('"');
                        } else {
                            inQuotes = false;
                            if (next != -1) {
                                reader.unread(next);
                            }
                        }
                    } else {
                        value.append(c);
                    }
                    lastCharWasDelimiter = false;
                    continue;
                }

                if (c == ',') {
                    row.add(value.toString());
                    value.setLength(0);
                    lastCharWasDelimiter = true;
                    continue;
                }

                if (c == '\n' || c == '\r') {
                    if (c == '\r') {
                        int next = reader.read();
                        if (next != '\n' && next != -1) {
                            reader.unread(next);
                        }
                    }
                    row.add(value.toString());
                    value.setLength(0);
                    result.add(row);
                    row = new ArrayList<>();
                    lastCharWasDelimiter = false;
                    continue;
                }

                if (c == '"') {
                    if (value.length() == 0) {
                        inQuotes = true;
                    } else {
                        value.append(c);
                    }
                    lastCharWasDelimiter = false;
                    continue;
                }

                value.append(c);
                lastCharWasDelimiter = false;
            }

            if (!sawAnyChar) {
                return List.of();
            }

            if (lastCharWasDelimiter || value.length() > 0 || !row.isEmpty()) {
                row.add(value.toString());
                result.add(row);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }
}
