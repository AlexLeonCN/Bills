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
        int maxColumns = 0;
        int lastRowNum = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                result.add(new ArrayList<>());
                continue;
            }
            int lastCellNum = Math.max(row.getLastCellNum(), 0);
            maxColumns = Math.max(maxColumns, lastCellNum);
            List<String> values = new ArrayList<>(lastCellNum);
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String value = cell == null ? "" : formatter.formatCellValue(cell);
                values.add(value);
            }
            result.add(values);
        }
        normalizeRowLengths(result, maxColumns);
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
        try (PushbackReader reader = new PushbackReader(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)), 1)) {
            List<List<String>> rows = parseCsv(reader);
            if (rows.isEmpty()) {
                return rows;
            }
            normalizeRowLengths(rows);
            return rows;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<List<String>> parseCsv(PushbackReader reader) throws IOException {
        CsvParseState state = new CsvParseState();
        int ch;
        while ((ch = reader.read()) != -1) {
            state.sawAnyChar = true;
            processChar(reader, state, (char) ch);
        }
        return finalizeCsv(state);
    }

    private static void processChar(PushbackReader reader, CsvParseState state, char c) throws IOException {
        if (state.inQuotes) {
            handleQuotedChar(reader, state, c);
            return;
        }
        if (c == ',') {
            addValue(state);
            state.lastCharWasDelimiter = true;
            return;
        }
        if (c == '\n' || c == '\r') {
            handleLineBreak(reader, state, c);
            return;
        }
        if (c == '"') {
            if (state.value.length() == 0) {
                state.inQuotes = true;
            } else {
                state.value.append(c);
            }
            state.lastCharWasDelimiter = false;
            return;
        }
        state.value.append(c);
        state.lastCharWasDelimiter = false;
    }

    private static void handleQuotedChar(PushbackReader reader, CsvParseState state, char c) throws IOException {
        if (c == '"') {
            int next = reader.read();
            if (next == '"') {
                state.value.append('"');
            } else {
                state.inQuotes = false;
                if (next != -1) {
                    reader.unread(next);
                }
            }
        } else {
            state.value.append(c);
        }
        state.lastCharWasDelimiter = false;
    }

    private static void handleLineBreak(PushbackReader reader, CsvParseState state, char c) throws IOException {
        if (c == '\r') {
            int next = reader.read();
            if (next != '\n' && next != -1) {
                reader.unread(next);
            }
        }
        addValue(state);
        state.result.add(state.row);
        state.row = new ArrayList<>();
        state.lastCharWasDelimiter = false;
    }

    private static void addValue(CsvParseState state) {
        state.row.add(state.value.toString());
        state.value.setLength(0);
    }

    private static List<List<String>> finalizeCsv(CsvParseState state) {
        if (!state.sawAnyChar) {
            return List.of();
        }
        if (state.lastCharWasDelimiter || state.value.length() > 0 || !state.row.isEmpty()) {
            addValue(state);
            state.result.add(state.row);
        }
        return state.result;
    }

    private static void normalizeRowLengths(List<List<String>> rows) {
        int maxColumns = 0;
        for (List<String> row : rows) {
            maxColumns = Math.max(maxColumns, row.size());
        }
        normalizeRowLengths(rows, maxColumns);
    }

    private static void normalizeRowLengths(List<List<String>> rows, int maxColumns) {
        if (maxColumns <= 0) {
            return;
        }
        for (List<String> row : rows) {
            int missing = maxColumns - row.size();
            for (int i = 0; i < missing; i++) {
                row.add("");
            }
        }
    }

    private static final class CsvParseState {
        private final List<List<String>> result = new ArrayList<>();
        private List<String> row = new ArrayList<>();
        private final StringBuilder value = new StringBuilder();
        private boolean inQuotes;
        private boolean lastCharWasDelimiter;
        private boolean sawAnyChar;
    }
}
