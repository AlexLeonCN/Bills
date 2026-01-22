package org.alex.bills.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Ledger {
    private Long id;
    private String name;
    private String desc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
