package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.time.LocalDateTime;

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

    private SubTask(String name, String description, TaskStatus status, int id, int epicId, LocalDateTime startTime, int taskDuration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.epicId = epicId;
        this.startTime = startTime;
        this.taskDuration = taskDuration;
    }

    public SubTask() {

    }

    public SubTask(String name, String description, TaskStatus status, int epicId, LocalDateTime startTime, int taskDuration) {
        super(name, description, status, startTime, taskDuration);
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
        return csvString.toString().replace("null", "");
    }

    @Override
    public SubTask fromCSVStringFormat(String line) {
        String[] parsedLine = line.split(";");
        int id = Integer.parseInt(parsedLine[0]);
        String name = parsedLine[2];
        TaskStatus status = TaskStatus.valueOf(parsedLine[3]);
        String description = parsedLine[4];
        LocalDateTime startTime = LocalDateTime.parse(parsedLine[5]);
        int taskDuration = Integer.parseInt(parsedLine[6]);
        int epicId = Integer.parseInt(parsedLine[7]);
        return new SubTask(name, description, status, id, epicId, startTime, taskDuration);
    }
}
