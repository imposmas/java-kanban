package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private static Task task1;
    private static Task task2;
    private static Task task3;
    int taskId1;
    int taskId2;
    int taskId3;
    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager taskManager;
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
    void historyDoesNotStoreDuplicatesTest() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        assertTrue(historyManager.getTasksHistory().size() == 1);
    }

    @Test
    void historyNodeContainsRefToFirstNodeTest() {
        historyManager.addToHistory(taskManager.getTask(taskId1));
        assertTrue(historyManager.getTasksHistory().get(taskId1) == historyManager.getFirst());
    }

    @Test
    void historyNodeContainsRefToLastNodeTest() {
        historyManager.addToHistory(taskManager.getTask(taskId1));
        historyManager.addToHistory(taskManager.getTask(taskId2));
        assertTrue(historyManager.getTasksHistory().get(taskId2) == historyManager.getLast());
    }

    @Test
    void historyNodeRemoveTest() {
        historyManager.addToHistory(taskManager.getTask(taskId1));
        historyManager.addToHistory(taskManager.getTask(taskId2));
        assertTrue(historyManager.getTasksHistory().get(taskId2) == historyManager.getLast());
        historyManager.remove(taskId2);
        assertTrue(historyManager.getTasksHistory().get(taskId1) == historyManager.getLast());
    }

    @Test
    void historyNodeRemoveMiddleNode() {
        historyManager.addToHistory(taskManager.getTask(taskId1));
        historyManager.addToHistory(taskManager.getTask(taskId2));
        historyManager.addToHistory(taskManager.getTask(taskId3));
        historyManager.remove(taskId2);
        assertTrue(historyManager.getTasksHistory().get(taskId1) == historyManager.getFirst()
                && historyManager.getTasksHistory().get(taskId3) == historyManager.getLast());
    }

}