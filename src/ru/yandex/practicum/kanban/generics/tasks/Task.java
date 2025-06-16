package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;
    protected LocalDateTime startTime;
    protected Duration taskDuration;

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Task taskCopy) {
        this.name = taskCopy.name;
        this.description = taskCopy.description;
        this.status = taskCopy.status;
        this.id = taskCopy.id;
        this.startTime = taskCopy.startTime;
        this.taskDuration = taskCopy.taskDuration;
    }

    public Task(int id, Task taskCopy) {
        this.name = taskCopy.name;
        this.description = taskCopy.description;
        this.status = taskCopy.status;
        this.id = id;
        this.startTime = taskCopy.startTime;
        this.taskDuration = taskCopy.taskDuration;
    }

    public Task(TaskStatus status, Task taskCopy) {
        this.name = taskCopy.name;
        this.description = taskCopy.description;
        this.status = status;
        this.id = taskCopy.id;
        this.startTime = taskCopy.startTime;
        this.taskDuration = taskCopy.taskDuration;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration taskDuration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.taskDuration = taskDuration;
    }

    private Task(String name, String description, TaskStatus status, int id, LocalDateTime startTime, Duration taskDuration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.taskDuration = taskDuration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getTaskDuration() {
        return taskDuration;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plusMinutes(this.taskDuration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskId=" + id +
                '}';
    }

    public String toCSVStringFormat() {
        StringBuilder csvString = new StringBuilder();
        csvString.append(getId()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getTaskType()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getName()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getStatus()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getDescription()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getStartTime()).append(FileConstants.CSV_DELIMITER);
        csvString.append(getTaskDuration().toMinutes()).append(FileConstants.CSV_DELIMITER);

        return csvString.toString().replace("null", "");
    }

    protected String getTaskType() {
        return getClass().getSimpleName().toUpperCase();
    }

    public Task fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        int id = Integer.parseInt(parsedLine[0]);
        String name = parsedLine[2];
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        String description = parsedLine[4];
        LocalDateTime startTime = LocalDateTime.parse(parsedLine[5]);
        Duration taskDuration = Duration.ofMinutes(Integer.parseInt(parsedLine[6]));
        return new Task(name, description, status, id, startTime, taskDuration);
    }

}
