package ru.yandex.practicum.kanban.exceptions;

public class FileSaveException extends RuntimeException {
    public FileSaveException(String message) {
        super(message);
    }
}
