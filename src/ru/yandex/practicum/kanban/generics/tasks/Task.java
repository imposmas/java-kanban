package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;

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
    }

    public Task(int id, Task taskCopy) {
        this.name = taskCopy.name;
        this.description = taskCopy.description;
        this.status = taskCopy.status;
        this.id = id;
    }

    public Task(TaskStatus status, Task taskCopy) {
        this.name = taskCopy.name;
        this.description = taskCopy.description;
        this.status = status;
        this.id = taskCopy.id;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    private Task(String name, String description, TaskStatus status, int id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    private void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
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
        return csvString.toString();
    }

    protected String getTaskType() {
        return getClass().getSimpleName().toUpperCase();
    }

    public Task fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        String name = parsedLine[2];
        String description = parsedLine[4];
        int id = Integer.parseInt(parsedLine[0]);
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        return new Task(name, description, status, id);
    }

}
