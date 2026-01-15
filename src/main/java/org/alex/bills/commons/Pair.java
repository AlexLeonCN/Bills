package org.alex.bills.commons;

public class Pair<T> {
    private final T key;
    private final T value;

    public Pair(T key, T value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
