package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.TaskStatus;

public class SubTask extends Task{

    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
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
}
