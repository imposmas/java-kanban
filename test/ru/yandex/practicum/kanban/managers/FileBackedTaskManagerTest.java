package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.exceptions.FileSaveException;
import ru.yandex.practicum.kanban.generics.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    public static final String FILE_NAME = "file_task_tracker_test.csv";
    public static final Path FILE_PATH = Paths.get(FILE_NAME);

    Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
    Task task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #2", "Task2 description", TaskStatus.NEW);
    Task task3 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #3", "Task3 description", TaskStatus.NEW);

    @BeforeEach
    void deleteGeneratedFile() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
        ;
    }

    @AfterEach
    void deleteGeneratedFileAfter() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
        ;
    }

    @Test
    void loadFromFileWithNotExistingFileTest() {
        assertNotNull(FileBackedTaskManager.loadFromFile(FILE_PATH.toFile()));
    }

    @Test
    void loadFromEmptyFileTest() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_PATH.toFile(), StandardCharsets.UTF_8))) {
            bufferedWriter.write(FileConstants.CSV_HEADER);
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new FileSaveException("Произошла ошибка во время записи файла");
        }
        assertNotNull(FileBackedTaskManager.loadFromFile(FILE_PATH.toFile()));
    }

    @Test
    void saveEmptyFileTest() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        int taskId1 = taskManager.addNewTask(task1);
        taskManager.deleteTask(taskId1);
        assertTrue(Files.exists(FILE_PATH));
    }

    @Test
    void saveTasksToFileTest() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());

        int taskId1 = fileTaskManager.addNewTask(task1);
        int taskId2 = fileTaskManager.addNewTask(task2);
        int taskId3 = fileTaskManager.addNewTask(task3);

        List<String> lines = readLinesFromFile(FILE_PATH.toFile());
        assertTrue(lines.get(0).equals(FileConstants.CSV_HEADER));
        assertTrue(lines.get(1).substring(0, 1).equals(String.valueOf(taskId1)));
        assertTrue(lines.get(2).substring(0, 1).equals(String.valueOf(taskId2)));
        assertTrue(lines.get(3).substring(0, 1).equals(String.valueOf(taskId3)));
    }

    @Test
    void loadTaskFromFile() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());

        int taskId1 = fileTaskManager.addNewTask(task1);
        fileTaskManager.updateTask(new Task(TaskStatus.DONE, fileTaskManager.getTask(taskId1)));
        FileBackedTaskManager loadedFromFileManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        assertTrue(loadedFromFileManager.findTaskById(taskId1).getId() == taskId1);
    }

    @Test
    void saveHistoryToFileTest() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        int taskId1 = fileTaskManager.addNewTask(task1);
        fileTaskManager.updateTask(new Task(TaskStatus.DONE, fileTaskManager.getTask(taskId1)));
        List<String> lines = readLinesFromFile(FILE_PATH.toFile());
        assertTrue(lines.get(lines.size() - 1).equals(String.valueOf(taskId1)));
    }


    List<String> readLinesFromFile(File file) {
        List<String> words = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                words.add(fileReader.readLine());
            }
            return words;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return null;
    }

}