package ru.yandex.practicum.kanban.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersUtilsTest {

    @Test
    void TaskManagerIsCreated() {
        assertNotNull(ManagersUtils.getDefault());
    }

    @Test
    void HistoryManagerIsCreated() {
        assertNotNull(ManagersUtils.getDefaultHistory());
    }

}