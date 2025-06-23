package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.List;
import java.util.TreeSet;

public interface TasksManager {
    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    TreeSet<Task> getPrioritizedTasks();

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    HistoryManager getHistoryManager();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();


}
