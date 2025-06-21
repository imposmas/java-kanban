package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private static Task task1;
    private static Task task2;
    private static Task task3;
    protected InMemoryTaskManager taskManager;
    int taskId1;
    int taskId2;
    int taskId3;
    LocalDateTime taskStartDate = LocalDateTime.of(2025, 7, 1, 8, 0);
    Duration taskDuration = Duration.ofMinutes(30);
    private InMemoryHistoryManager historyManager;
    private Task task = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, taskStartDate, taskDuration);


    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        LocalDateTime task1StartDate = LocalDateTime.of(2025, 3, 1, 8, 0);
        Duration task1duration = Duration.ofMinutes(30);
        Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, task1duration);
        LocalDateTime task2StartDate = LocalDateTime.of(2025, 3, 1, 7, 0);
        Duration task2duration = Duration.ofMinutes(30);
        Task task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #2", "Task2 description", TaskStatus.NEW, task2StartDate, task2duration);
        LocalDateTime task3StartDate = LocalDateTime.of(2025, 3, 2, 7, 0);
        Duration task3duration = Duration.ofMinutes(30);
        Task task3 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #3", "Task3 description", TaskStatus.NEW, task3StartDate, task3duration);

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