package org.alex.bills.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class Bill {
    private Long id;
    private String ledger;
    private String category;
    private String subCategory;
    private String currency;
    private BigDecimal amount;
    private String account;
    private String recorder;
    private LocalDate billDate;
    private LocalTime billTime;
    private String tag;
    private String remark;
    private String incomeExpense;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
