package org.alex.bills.exception;

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

    public BaseException(Pair<?> pair) {
        this(extractCode(pair), extractMessage(pair));
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

    private static Integer extractCode(Pair<?> pair) {
        if (pair == null) {
            return null;
        }
        Object key = pair.getKey();
        if (key instanceof Number) {
            return ((Number) key).intValue();
        }
        return null;
    }

    private static String extractMessage(Pair<?> pair) {
        if (pair == null) {
            return null;
        }
        Object value = pair.getValue();
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
