package ru.yandex.practicum.kanban.generics.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    SubTask SubTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, 1);


    @Test
    void getEpicId() {
        assertEquals(1, SubTask1.getEpicId());
    }

}