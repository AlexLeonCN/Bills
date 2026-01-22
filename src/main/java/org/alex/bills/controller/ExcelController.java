package org.alex.bills.controller;

import org.alex.bills.commons.ResultInfo;
import org.alex.bills.commons.exception.ImportException;
import org.alex.bills.model.BillImportResult;
import org.alex.bills.service.ExcelService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultInfo<BillImportResult> importBills(@RequestParam("file") MultipartFile file) {
        try {
            return ResultInfo.ofSuccess(excelService.importBills(file));
        } catch (ImportException e) {
            return ResultInfo.ofError(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResultInfo.ofError(400, e.getMessage());
        } catch (Exception e) {
            return ResultInfo.ofError(500, "导入失败");
        }
    }
}
