package org.alex.bills.service;

import org.alex.bills.model.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface BillService {
    ImportResult importBills(MultipartFile file);
}
