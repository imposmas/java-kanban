package ru.yandex.practicum.kanban.generics.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {

    LocalDateTime subTask1StartDate = LocalDateTime.of(2025, 3, 1, 9, 0);
    ;
    SubTask SubTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, 1, subTask1StartDate, 30);


    @Test
    void getEpicId() {
        assertEquals(1, SubTask1.getEpicId());
    }

}