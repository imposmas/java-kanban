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
    private static Task task2;
    private static Task task3;
    int taskId1;
    int taskId2;
    int taskId3;

    private Task task = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);


    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #2", "Task2 description", TaskStatus.NEW);
        task3 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #3", "Task3 description", TaskStatus.NEW);
        taskId1 = taskManager.addNewTask(task1);
        taskId2 = taskManager.addNewTask(task2);
        taskId3 = taskManager.addNewTask(task3);
    }

    @Test
    void addAndGetHistoryTest() {
        historyManager.addToHistory(task);
        assertTrue(historyManager.getHistory().get(0).equals(task));
    }

    @Test
    void HistoryDoesNotStoreDuplicatesTest(){
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        assertTrue(historyManager.tasksHistory.size() == 1);
    }

    @Test
    void HistoryNodeContainsRefToFirstNodeTest(){
        historyManager.addToHistory(task1);
        assertTrue(historyManager.tasksHistory.get(taskId1)==historyManager.first);
    }

    @Test
    void HistoryNodeContainsRefToLastNodeTest(){
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        assertTrue(historyManager.tasksHistory.get(taskId2)==historyManager.last);
    }

    @Test
    void HistoryNodeRemoveTest(){
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        assertTrue(historyManager.tasksHistory.get(taskId2)==historyManager.last);
        historyManager.remove(taskId2);
        assertTrue(historyManager.tasksHistory.get(taskId1)==historyManager.last);
    }

}