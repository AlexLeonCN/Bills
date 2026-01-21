package org.alex.bills.constants;

import java.util.List;
import java.util.Set;

public final class BillImportConstants {
    public static final String HEADER_LEDGER = "账本";
    public static final String HEADER_CATEGORY = "分类";
    public static final String HEADER_SUB_CATEGORY = "子分类";
    public static final String HEADER_CURRENCY = "货币";
    public static final String HEADER_AMOUNT = "金额";
    public static final String HEADER_ACCOUNT = "账户";
    public static final String HEADER_RECORDER = "记录人";
    public static final String HEADER_DATE = "日期";
    public static final String HEADER_TIME = "时间";
    public static final String HEADER_TAG = "标签";
    public static final String HEADER_REMARK = "备注";
    public static final String HEADER_INCOME_EXPENSE = "收支";

    public static final List<String> HEADERS = List.of(
            HEADER_LEDGER,
            HEADER_CATEGORY,
            HEADER_SUB_CATEGORY,
            HEADER_CURRENCY,
            HEADER_AMOUNT,
            HEADER_ACCOUNT,
            HEADER_RECORDER,
            HEADER_DATE,
            HEADER_TIME,
            HEADER_TAG,
            HEADER_REMARK,
            HEADER_INCOME_EXPENSE
    );

    public static final Set<String> HEADER_SET = Set.copyOf(HEADERS);

    private BillImportConstants() {
    }
}
