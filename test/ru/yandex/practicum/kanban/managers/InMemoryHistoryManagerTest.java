package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager ;
    private InMemoryTaskManager taskManager ;

    private static Task task1;
    int taskId1;

    private Task task = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        taskId1 = taskManager.addNewTask(task1);
    }

    @Test
    void addAndGetHistoryTest() {
        historyManager.addToHistory(task);
        assertTrue(historyManager.getHistory().get(0).equals(task));
    }

    @Test
    void notPossibleToAdd11Elements(){
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        assertTrue(historyManager.getHistory().size() == 10);
    }

    @Test
    void storePreviousTaskVersion(){
        taskManager.getTask(taskId1);
        final Task taskNew = taskManager.getTask(taskId1);
        taskNew.setStatus(TaskStatus.DONE);
        taskManager.updateTask(taskNew);
        taskManager.getTask(taskId1);
        taskManager.historyManager.getHistory();
        TaskStatus previousTaskStatus = taskManager.historyManager.getHistory().get(1).getStatus();
        TaskStatus newTaskStatus = taskManager.historyManager.getHistory().get(2).getStatus();
        assertTrue(!previousTaskStatus.equals(newTaskStatus));
    }

}