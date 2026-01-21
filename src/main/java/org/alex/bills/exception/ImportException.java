package org.alex.bills.exception;

import org.alex.bills.commons.Pair;
import org.alex.bills.commons.ResultInfo;

public class ImportException extends BaseException {
    public ImportException(Integer code, String message) {
        super(code, message);
    }

    public ImportException(Pair<?, ?> pair) {
        super(pair);
    }

    public ImportException(ResultInfo<?> resultInfo) {
        super(resultInfo);
    }
}
