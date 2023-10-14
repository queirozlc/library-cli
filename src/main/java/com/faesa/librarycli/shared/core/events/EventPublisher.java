package com.faesa.librarycli.shared.core.events;

public interface EventPublisher<T extends Event> {
    void publish(T event);

    void subscribe(EventListener<T> listener);

    void unsubscribe(EventListener<T> listener);

    void unsubscribeAll();

    void notifySubscribers(T event);
}
