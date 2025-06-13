package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.exceptions.OverlapException;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TasksManagerTest<T extends TasksManager> {

    private static Task task1;
    private static Epic epic1;
    private static SubTask SubTask1;
    private static SubTask SubTask2;
    private static SubTask SubTask3;
    protected InMemoryTaskManager taskManager;
    LocalDateTime task1StartDate;
    LocalDateTime subTask1StartDate;
    LocalDateTime subTask2StartDate;
    LocalDateTime subTask3StartDate;
    int taskId1;
    int epicId1;
    int SubTaskId1;
    int SubTaskId2;
    int SubTaskId3;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        task1StartDate = LocalDateTime.of(2025, 3, 1, 8, 0);
        task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, 30);
        taskId1 = taskManager.addNewTask(task1);
        epic1 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        epicId1 = taskManager.addNewEpic(epic1);
        subTask1StartDate = LocalDateTime.of(2025, 3, 1, 9, 0);
        SubTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, epicId1, subTask1StartDate, 30);
        subTask2StartDate = LocalDateTime.of(2025, 3, 1, 10, 0);
        SubTask2 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #2-1", "SubTask1 description", TaskStatus.NEW, epicId1, subTask2StartDate, 30);
        SubTaskId1 = taskManager.addNewSubTask(SubTask1);
        SubTaskId2 = taskManager.addNewSubTask(SubTask2);
        subTask3StartDate = LocalDateTime.of(2025, 3, 1, 11, 0);
        SubTask3 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #3-1", "SubTask1 description", TaskStatus.NEW, epicId1, subTask3StartDate, 30);
        SubTaskId3 = taskManager.addNewSubTask(SubTask3);
    }

    @Test
    void addNewTaskTest() {
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
        task1StartDate = LocalDateTime.of(2025, 4, 1, 8, 0);
        Task task2 = new Task("Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, 30);
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
        subTask2StartDate = LocalDateTime.of(2025, 3, 1, 10, 0);
        SubTask SubTask3 = new SubTask("SubTask #3-1", "SubTask1 description", TaskStatus.NEW, SubTaskId2, subTask2StartDate, 30);
        Integer SubtaskId3 = taskManager.addNewSubTask(SubTask3);
        assertNull(SubtaskId3);
    }

    @Test
    void notPossibleToSetId() {
        task1StartDate = LocalDateTime.of(2025, 9, 1, 8, 0);
        Task task3 = new Task("Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, 30);
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

    @Test
    void epicStatusIsNewIfAllSubTasksAreNew() {
        assertEquals(TaskStatus.NEW, taskManager.getEpic(epicId1).getStatus());
    }

    @Test
    void epicStatusIsDoneIfAllSubTasksAreDone() {
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId1), TaskStatus.DONE));
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId2), TaskStatus.DONE));
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId3), TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epicId1).getStatus());
    }

    @Test
    void epicStatusIsNewIfSubTasksAreNewAndDone() {
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId2), TaskStatus.DONE));
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId3), TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId1).getStatus());
    }

    @Test
    void epicStatusIsInProgressIfSubTasksAreInProgress() {
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId1), TaskStatus.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId2), TaskStatus.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(taskManager.getSubTask(SubTaskId3), TaskStatus.IN_PROGRESS));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId1).getStatus());
    }

    @Test
    void prioritizedTasksAreStoredInCorrectOrder() {
        LocalDateTime task1start = taskManager.getPrioritizedTasks().getFirst().getStartTime();
        LocalDateTime task2start = taskManager.prioritizedTasks.getLast().getStartTime();
        assertTrue(task1start.isBefore(task2start));
    }

    @Test
    void epicDurationIsSumOfSubTasksDurations() {
        assertTrue(SubTask1.getTaskDuration() + SubTask2.getTaskDuration() + SubTask3.getTaskDuration() == taskManager.getEpic(epicId1).getTaskDuration());
    }

    @Test
    void overlapExceptionTest() {
        LocalDateTime taskStartDate = LocalDateTime.of(2025, 3, 1, 8, 15);
        Task task4 = new Task("Task #1", "Task1 description", TaskStatus.NEW, taskStartDate, 30);
        assertThrows(OverlapException.class, () -> {
            taskManager.addNewTask(task4);
        }, "Overlapping exists between added and existing tasks");
    }

}