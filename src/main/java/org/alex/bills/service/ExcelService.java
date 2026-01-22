package org.alex.bills.service;

import org.alex.bills.model.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    ImportResult importBills(MultipartFile file);
}
