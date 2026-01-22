package org.alex.bills.model;

import lombok.Data;

@Data
public class ImportResult {
    private int totalRows;
    private int importedRows;
    private int skippedRows;

    public ImportResult(int totalRows, int importedRows, int skippedRows) {
        this.totalRows = totalRows;
        this.importedRows = importedRows;
        this.skippedRows = skippedRows;
    }
}
