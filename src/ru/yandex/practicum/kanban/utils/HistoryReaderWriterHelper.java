package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.managers.HistoryManager;

import java.util.Arrays;
import java.util.List;

public class HistoryReaderWriterHelper {

    public static String fromMemoryToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        List<String> viewedIds = history.stream().map(task -> String.valueOf(task.getId())).toList();
        return String.join(FileConstants.CSV_DELIMITER, viewedIds);
    }

    public static List<Integer> fromStringToMemory(String line) {
        if (line != null) {
            String[] viewedIds = line.split(FileConstants.CSV_DELIMITER);
            return Arrays.stream(viewedIds).map(task -> Integer.parseInt(task)).toList();
        } else {
            return null;
        }
    }
}
