package org.alex.bills.service;

import org.alex.bills.model.BillImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    BillImportResult importBills(MultipartFile file);
}
