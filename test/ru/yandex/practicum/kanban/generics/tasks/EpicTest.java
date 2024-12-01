package ru.yandex.practicum.kanban.generics.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TasksManager;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic1 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
    Epic epic2 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");

    @BeforeEach
    void setUp() {
        List<Integer> expectedSubTasks = new ArrayList<>();
        expectedSubTasks.add(1);
        epic1.setSubTasks(expectedSubTasks);
    }

    @Test
    void getSubTasks() {
        assertNotNull(epic1.getSubTasks());
    }


}