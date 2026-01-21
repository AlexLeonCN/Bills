package org.alex.bills.commons;

public interface ErrorConstant {
    interface Import {
        Pair<Integer, String> FILE_EMPTY = new Pair<>(40001, "导入文件为空");
        Pair<Integer, String> FILE_CONTENT_EMPTY = new Pair<>(40002, "导入文件内容为空");
        Pair<Integer, String> FILE_TYPE_UNSUPPORTED = new Pair<>(40003, "不支持的文件类型");
        Pair<Integer, String> HEADER_DUPLICATE = new Pair<>(40004, "表头重复");
        Pair<Integer, String> HEADER_INVALID = new Pair<>(40005, "表头校验失败");
    }

    interface Common {
        Pair<Integer, String> SYSTEM_ERROR = new Pair<>(50000, "系统异常");
    }
}
