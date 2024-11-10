package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static InMemoryTaskManager taskManager = new InMemoryTaskManager();

    private static Task task1;
    int taskId1;
    private static Epic epic1;
    int epicId1;
    private static SubTask SubTask1;
    int SubTaskId1;
    private static SubTask SubTask2;
    int SubTaskId2;

    @BeforeEach
    void setUp() {
        task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        taskId1 = taskManager.addNewTask(task1);
        epic1 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        epicId1 = taskManager.addNewEpic(epic1);
        SubTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTask2 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #2-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTaskId1 = taskManager.addNewSubTask(SubTask1);
        SubTaskId2 = taskManager.addNewSubTask(SubTask2);
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
        task1.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task1.getStatus());
    }

    @Test
    void updateSubtaskAndEpicStatusTest() {
        SubTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(SubTask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
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
        assertEquals(null,taskManager.getSubTask(SubTaskId1));
    }


    @Test
    void deleteAllEpicsTest() {
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void taskIsEqualsAfterAddToManager(){
        Task task2 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        assertTrue(task2.getName()=="Task #1" && task2.getDescription()=="Task1 description"
                && task2.getStatus() ==TaskStatus.NEW);
    }

}