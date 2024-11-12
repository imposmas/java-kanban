package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addToHistory(Task task);

}
