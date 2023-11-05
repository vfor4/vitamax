package se.magnus.api.event;

import java.time.ZonedDateTime;

public class Event<K, T> {
    public enum Type {CREATE, DELETE};

    private final Event.Type type;
    private final K key;
    private final T data;
    private final ZonedDateTime eventCreatedAt;

    public Event(final Type type, final K key, final T data) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = ZonedDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}