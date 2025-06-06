package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, Epic epic) {
        super(id, epic);
    }

    public Epic(Epic epic, TaskStatus taskStatus) {
        super(epic);
        this.status = taskStatus;
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
                ", subtasks=" + subTasks +
                '}';
    }
}
