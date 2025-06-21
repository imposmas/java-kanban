package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasks = new ArrayList<>();

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, Epic epic) {
        super(id, epic);
    }

    public Epic(Epic epic, TaskStatus taskStatus) {
        super(epic);
        this.subTasks = epic.getSubTasks();
        this.status = taskStatus;
    }

    private Epic(String name, String description, TaskStatus status, int id, LocalDateTime epicStartTime, Duration epicDuration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = epicStartTime;
        this.taskDuration = epicDuration;
    }

    public Epic(Epic epic, LocalDateTime epicStartTime, Duration epicDuration) {
        super(epic);
        this.subTasks = epic.getSubTasks();
        this.status = epic.getStatus();
        this.startTime = epicStartTime;
        this.taskDuration = epicDuration;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", taskId=" + super.getId() +
                ", subtasks=" + subTasks.toString() +
                '}';
    }

    @Override
    public String toCSVStringFormat() {
        StringBuilder csvString = new StringBuilder(super.toCSVStringFormat());
        csvString.append(FileConstants.CSV_DELIMITER);
        csvString.append(getSubTasks().toString());
        return csvString.toString().replace("null", "");
    }

    @Override
    public Epic fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        int id = Integer.parseInt(parsedLine[0]);
        String name = parsedLine[2];
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        String description = parsedLine[4];
        LocalDateTime startTime = LocalDateTime.parse(parsedLine[5]);
        Duration taskDuration = Duration.ofMinutes(Long.parseLong(parsedLine[6]));
        return new Epic(name, description, status, id, startTime, taskDuration);
    }
}
