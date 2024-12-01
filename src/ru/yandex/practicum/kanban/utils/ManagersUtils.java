package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TasksManager;

public class ManagersUtils {

    public static TasksManager getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
