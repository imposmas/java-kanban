package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {
    List<T> getHistory();

    void addToHistory(T task);

}
