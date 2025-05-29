package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.managers.FileBackedTaskManager;
import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.managers.TasksManager;

public class ManagersUtils {

    public static TasksManager getDefault() {
        return FileBackedTaskManager.loadFromFile(FileConstants.FILE_PATH.toFile());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}