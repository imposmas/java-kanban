package ru.yandex.practicum.kanban.generics.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
    Task task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);

    @BeforeEach
    void setUp() {
    }

    @Test
    void getName() {
        assertEquals("ru.yandex.practicum.kanban.generics.tasks.Task #1", task1.getName());
    }

    @Test
    void setName() {
        task1.setName("Task #1");
        assertEquals("Task #1", task1.getName());
    }

    @Test
    void getDescription() {
        assertEquals("Task1 description", task1.getDescription());
    }

    @Test
    void setDescription() {
        task1.setDescription("Task #1");
        assertEquals("Task #1", task1.getDescription());
    }

    @Test
    void getStatus() {
        assertEquals(TaskStatus.NEW, task1.getStatus());
    }

    @Test
    void setStatus() {
        task1.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task1.getStatus());
    }

    @Test
    void getAndSetId() {
        task1.setId(1);
        assertEquals(1, task1.getId());
    }

    @Test
    void shouldBeEqualsWithTheSameId(){
        task1.setId(1);
        task2.setId(1);
        assertTrue(task1.equals(task2));
    }

}