package org.alex.bills.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.alex.bills.commons.ErrorConstant;
import org.alex.bills.commons.Pair;
import org.alex.bills.commons.utils.SnowflakeIdGenerator;
import org.alex.bills.constants.BillImportConstants;
import org.alex.bills.exception.ImportException;
import org.alex.bills.mapper.BillMapper;
import org.alex.bills.model.Bill;
import org.alex.bills.model.BillImportResult;
import org.alex.bills.service.ExcelService;
import org.alex.bills.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelServiceImpl implements ExcelService {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final SnowflakeIdGenerator ID_GENERATOR = new SnowflakeIdGenerator(1, 1);

    private final BillMapper billMapper;

    public ExcelServiceImpl(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    @Override
    @Transactional
    public BillImportResult importBills(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImportException(ErrorConstant.Import.FILE_EMPTY);
        }
        List<List<String>> rows = readRows(file);
        if (rows.isEmpty()) {
            throw new ImportException(ErrorConstant.Import.FILE_CONTENT_EMPTY);
        }
        Map<String, Integer> headerIndex = buildHeaderIndex(rows.get(0));
        Map<String, BiConsumer<Bill, String>> parsers = buildHeaderParsers();
        List<Bill> bills = new ArrayList<>();
        int skippedRows = 0;
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (isRowBlank(row)) {
                skippedRows++;
                continue;
            }
            Bill bill = new Bill();
            bill.setId(ID_GENERATOR.nextId());
            LocalDateTime now = LocalDateTime.now();
            bill.setCreateTime(now);
            bill.setUpdateTime(now);
            for (String header : BillImportConstants.HEADERS) {
                Integer index = headerIndex.get(header);
                String value = index != null && index < row.size() ? row.get(index) : "";
                BiConsumer<Bill, String> parser = parsers.get(header);
                if (parser != null) {
                    parser.accept(bill, value);
                }
            }
            bills.add(bill);
        }
        if (!bills.isEmpty()) {
            billMapper.insertBatch(bills);
        }
        int totalRows = Math.max(rows.size() - 1, 0);
        return new BillImportResult(totalRows, bills.size(), skippedRows);
    }

    private List<List<String>> readRows(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String lowerName = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        try (InputStream inputStream = file.getInputStream()) {
            if (lowerName.endsWith(".csv")) {
                return ExcelUtils.csvToList(inputStream);
            }
            if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
                return readExcel(inputStream);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new ImportException(ErrorConstant.Import.FILE_TYPE_UNSUPPORTED);
    }

    private List<List<String>> readExcel(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() <= 0) {
                return List.of();
            }
            Sheet sheet = workbook.getSheetAt(0);
            return ExcelUtils.sheetToList(sheet);
        }
    }

    private Map<String, Integer> buildHeaderIndex(List<String> headerRow) {
        Map<String, Integer> headerIndex = new LinkedHashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            String header = normalizeHeader(headerRow.get(i));
            if (header.isEmpty()) {
                continue;
            }
            if (headerIndex.putIfAbsent(header, i) != null) {
                throw new ImportException(new Pair<>(ErrorConstant.Import.HEADER_DUPLICATE.getKey(),
                        ErrorConstant.Import.HEADER_DUPLICATE.getValue() + ": " + header));
            }
        }
        validateHeader(headerIndex.keySet());
        return headerIndex;
    }

    private void validateHeader(Set<String> headerSet) {
        if (BillImportConstants.HEADER_SET.equals(headerSet)) {
            return;
        }
        Set<String> missing = new LinkedHashSet<>(BillImportConstants.HEADER_SET);
        missing.removeAll(headerSet);
        Set<String> extra = new LinkedHashSet<>(headerSet);
        extra.removeAll(BillImportConstants.HEADER_SET);
        StringBuilder message = new StringBuilder("表头校验失败");
        if (!missing.isEmpty()) {
            message.append("，缺少: ").append(String.join("、", missing));
        }
        if (!extra.isEmpty()) {
            message.append("，多余: ").append(String.join("、", extra));
        }
        throw new ImportException(new Pair<>(ErrorConstant.Import.HEADER_INVALID.getKey(), message.toString()));
    }

    private Map<String, BiConsumer<Bill, String>> buildHeaderParsers() {
        Map<String, BiConsumer<Bill, String>> parsers = new LinkedHashMap<>();
        parsers.put(BillImportConstants.HEADER_LEDGER,
                (bill, value) -> bill.setLedger(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_CATEGORY,
                (bill, value) -> bill.setCategory(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_SUB_CATEGORY,
                (bill, value) -> bill.setSubCategory(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_CURRENCY,
                (bill, value) -> bill.setCurrency(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_AMOUNT,
                (bill, value) -> bill.setAmount(parseAmount(value)));
        parsers.put(BillImportConstants.HEADER_ACCOUNT,
                (bill, value) -> bill.setAccount(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_RECORDER,
                (bill, value) -> bill.setRecorder(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_DATE,
                (bill, value) -> bill.setBillDate(parseDate(value)));
        parsers.put(BillImportConstants.HEADER_TIME,
                (bill, value) -> bill.setBillTime(parseTime(value)));
        parsers.put(BillImportConstants.HEADER_TAG,
                (bill, value) -> bill.setTag(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_REMARK,
                (bill, value) -> bill.setRemark(trimToNull(value)));
        parsers.put(BillImportConstants.HEADER_INCOME_EXPENSE,
                (bill, value) -> bill.setIncomeExpense(trimToNull(value)));
        return parsers;
    }

    private boolean isRowBlank(List<String> row) {
        if (row == null || row.isEmpty()) {
            return true;
        }
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        String header = value.trim();
        if (!header.isEmpty() && header.charAt(0) == '\uFEFF') {
            header = header.substring(1).trim();
        }
        return header;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private BigDecimal parseAmount(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        String cleaned = normalized.replace(",", "");
        return new BigDecimal(cleaned);
    }

    private LocalDate parseDate(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        return LocalDate.parse(normalized);
    }

    private LocalTime parseTime(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        return LocalTime.parse(normalized, TIME_FORMATTER);
    }
}
