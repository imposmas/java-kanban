package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    private static Task task1;
    int taskId1;
    private static Epic epic1;
    int epicId1;
    private static SubTask SubTask1;
    int SubTaskId1;
    private static SubTask SubTask2;
    int SubTaskId2;
    private static SubTask SubTask3;
    int SubTaskId3;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        taskId1 = taskManager.addNewTask(task1);
        epic1 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        epicId1 = taskManager.addNewEpic(epic1);
        SubTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTask2 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #2-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTaskId1 = taskManager.addNewSubTask(SubTask1);
        SubTaskId2 = taskManager.addNewSubTask(SubTask2);
        SubTask3 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #3-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTaskId3 = taskManager.addNewSubTask(SubTask3);
    }

    @Test
    void addNewTask() {
        assertNotNull(taskManager.getTask(taskId1));
    }

    @Test
    void createAndRetrieveEpicWithSubtasksTest() {
        assertNotNull(taskManager.getEpic(epicId1));
        assertNotNull(taskManager.getEpicSubTasks(epicId1));
    }

    @Test
    void updateTaskStatusTest() {
        taskManager.updateTask(new Task(TaskStatus.DONE, taskManager.getTask(taskId1)));
        assertEquals(TaskStatus.DONE, taskManager.getTask(taskId1).getStatus());
    }

    @Test
    void updateSubtaskAndEpicStatusTest() {
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId1), TaskStatus.IN_PROGRESS));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId1).getStatus());
    }

    @Test
    void deleteTaskByIdTest() {
        taskManager.deleteTask(taskId1);
        assertNull(taskManager.getTask(taskId1));
    }

    @Test
    void deleteAllTasksTest() {
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteSubtaskByIdTest() {
        taskManager.deleteSubTask(SubTaskId1);
        assertEquals(null, taskManager.getSubTask(SubTaskId1));
    }


    @Test
    void deleteAllEpicsTest() {
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void taskIsEqualsAfterAddToManager() {
        Task task2 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        taskManager.addNewTask(task2);
        assertTrue(task2.getName().equals("Task #1"));
        assertTrue(task2.getDescription().equals("Task1 description"));
        assertTrue(task2.getStatus().equals(TaskStatus.NEW));
    }

    @Test
    void notPossibleToAddEpicToEpicAsSubtask() {
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId3), SubTaskId2));
        SubTask ExpectedSubTask = taskManager.getSubTask(SubTaskId2);
        assertTrue(ExpectedSubTask.getEpicId() != SubTaskId2);
    }

    @Test
    void notPossibleToAddSubtaskAsEpic() {
        SubTask SubTask3 = new SubTask("SubTask #3-1", "SubTask1 description", TaskStatus.NEW, SubTaskId2);
        Integer SubtaskId3 = taskManager.addNewSubTask(SubTask3);
        assertNull(SubtaskId3);
    }

    @Test
    void notPossibleToSetId() {
        Task task3 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        int inputId = 1000;
        int task3id = taskManager.addNewTask(new Task(inputId, task3));
        assertNotEquals(inputId, task3id);
    }

    @Test
    void notPossibleToDeleteEpicAndSaveSubtaskTest() {
        taskManager.deleteEpic(epicId1);
        assertTrue(taskManager.getEpicSubTasks(epicId1).isEmpty());
    }

    @Test
    void epicDoesNotStoreDeletedSubtasks() {
        taskManager.deleteSubTask(SubTaskId2);
        assertTrue(!taskManager.getEpicSubTasks(epicId1).contains(SubTaskId2));
    }


}