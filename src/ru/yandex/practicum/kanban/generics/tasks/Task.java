package ru.yandex.practicum.kanban.generics.tasks;

import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;

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

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return  Objects.equals(id, task.id) ;
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

}
