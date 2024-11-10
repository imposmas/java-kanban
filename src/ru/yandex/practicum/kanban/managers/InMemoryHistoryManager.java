package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T>{

    ArrayList<T> tasksHistory = new ArrayList<>();
    private static final int hitorySize = 10;

    @Override
    public List<T> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addToHistory(T task){
        if(tasksHistory.size() == hitorySize){
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
