package org.alex.bills.controller;

import org.alex.bills.commons.ResultInfo;
import org.alex.bills.commons.exception.ImportException;
import org.alex.bills.model.ImportResult;
import org.alex.bills.service.BillService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bill")
public class BillController {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultInfo<ImportResult> importBills(@RequestParam("file") MultipartFile file) {
        try {
            return ResultInfo.ofSuccess(billService.importBills(file));
        } catch (ImportException e) {
            return ResultInfo.ofError(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResultInfo.ofError(400, e.getMessage());
        } catch (Exception e) {
            return ResultInfo.ofError(500, "导入失败");
        }
    }
}
