package org.alex.bills.commons.exception;

import org.alex.bills.commons.Pair;
import org.alex.bills.commons.ResultInfo;

public class BaseException extends RuntimeException {
    private final Integer code;
    private final String message;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Pair<Integer, String> pair) {
        this(pair == null ? null : pair.getKey(),
                pair == null ? null : pair.getValue());
    }

    public BaseException(ResultInfo<?> resultInfo) {
        this(resultInfo == null ? null : resultInfo.getCode(),
                resultInfo == null ? null : resultInfo.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
