package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    ArrayList<Task> tasksHistory = new ArrayList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addToHistory(Task task){
        if(tasksHistory.size() == HISTORY_SIZE){
            tasksHistory.remove(0);
        }
        Task task2history = new Task(task);
        tasksHistory.add(task2history);
    }
}
