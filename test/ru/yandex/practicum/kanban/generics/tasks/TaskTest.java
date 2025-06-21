package ru.yandex.practicum.kanban.generics.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    LocalDateTime task1StartDate = LocalDateTime.of(2025, 3, 1, 8, 0);
    Duration task1duration = Duration.ofMinutes(30);
    Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, task1duration);


    @Test
    void getName() {
        assertEquals("ru.yandex.practicum.kanban.generics.tasks.Task #1", task1.getName());
    }


    @Test
    void getDescription() {
        assertEquals("Task1 description", task1.getDescription());
    }


    @Test
    void getStatus() {
        assertEquals(TaskStatus.NEW, task1.getStatus());
    }


}