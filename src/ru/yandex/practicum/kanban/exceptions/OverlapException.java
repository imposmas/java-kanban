package ru.yandex.practicum.kanban.exceptions;

public class OverlapException extends RuntimeException {
    public OverlapException(String message) {
        super(message);
    }
}
