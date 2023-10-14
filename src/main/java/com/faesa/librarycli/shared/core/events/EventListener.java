package com.faesa.librarycli.shared.core.events;

public interface EventListener<T> {
    void handle(T event);
}
