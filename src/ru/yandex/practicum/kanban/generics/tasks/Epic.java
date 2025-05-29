package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.constants.TaskStatus;

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

    private Epic(String name, String description, TaskStatus status, int id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    private void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
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
        return csvString.toString();
    }

    @Override
    public Epic fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        String name = parsedLine[2];
        String description = parsedLine[4];
        int id = Integer.parseInt(parsedLine[0]);
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        return new Epic(name, description, status, id);
    }
}
