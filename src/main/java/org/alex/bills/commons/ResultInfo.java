package org.alex.bills.commons;

public class ResultInfo<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    private ResultInfo(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultInfo<T> ofSuccess() {
        return new ResultInfo<>(true, 200, "success", null);
    }

    public static <T> ResultInfo<T> ofSuccess(T data) {
        return new ResultInfo<>(true, 200, "success", data);
    }

    public static <T> ResultInfo<T> ofError() {
        return new ResultInfo<>(false, 201, "error", null);
    }

    public static <T> ResultInfo<T> ofError(int code, String message) {
        return new ResultInfo<>(false, code, message, null);
    }

    public static <T> ResultInfo<T> ofError(Pair<?, ?> pair) {
        if (pair == null) {
            return ofError();
        }
        Object key = pair.getKey();
        Object value = pair.getValue();
        if (!(key instanceof Number) || !(value instanceof String)) {
            return ofError();
        }
        return ofError(((Number) key).intValue(), (String) value);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
