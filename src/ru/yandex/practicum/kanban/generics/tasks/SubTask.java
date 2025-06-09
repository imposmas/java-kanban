package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.TaskStatus;

public class SubTask extends Task {

    private int epicId;

    public SubTask(int id, SubTask subTask) {
        super(id, subTask);
        this.epicId = subTask.epicId;
    }

    public SubTask(SubTask subTask, int epicId) {
        super(subTask);
        this.epicId = epicId;
    }

    public SubTask(SubTask subTask1, TaskStatus taskStatus) {
        super(subTask1);
        this.epicId = subTask1.epicId;
        this.status = taskStatus;
    }

    private SubTask(String name, String description, TaskStatus status, int id, int epicId) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.epicId = epicId;
    }

    public SubTask() {

    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" + '\'' +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", taskId=" + super.getId() +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String toCSVStringFormat() {
        StringBuilder csvString = new StringBuilder(super.toCSVStringFormat());
        csvString.append(getEpicId());
        return csvString.toString();
    }

    @Override
    public SubTask fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        String name = parsedLine[2];
        String description = parsedLine[4];
        int id = Integer.parseInt(parsedLine[0]);
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        int epicId = Integer.parseInt(parsedLine[5]);
        return new SubTask(name, description, status, id, epicId);
    }
}
