package ru.yandex.practicum.kanban.generics.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    private List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public <T extends Task> Epic(T task) {
        super(task);
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
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
