package ru.yandex.practicum.kanban.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileConstants {

    public static final String FILE_NAME = "tasks.csv";
    public static final Path FILE_PATH = Paths.get(FILE_NAME);
    public static final String CSV_HEADER = "id;type;name;status;description;startTime;duration;epic;subtasks";
    public static final String CSV_DELIMITER = ";";
    public static final String HISTORY_HEADER = "viewed task's IDs";
}
