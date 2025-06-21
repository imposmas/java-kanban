package ru.yandex.practicum.kanban.managers;

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TasksManagerTest<FileBackedTaskManager> {

    public static final String FILE_NAME = "file_task_tracker_test.csv";
    public static final Path FILE_PATH = Paths.get(FILE_NAME);

    LocalDateTime task1StartDate = LocalDateTime.of(2025, 3, 1, 8, 0);
    Duration task1duration = Duration.ofMinutes(30);
    Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, task1duration);
    LocalDateTime task2StartDate = LocalDateTime.of(2025, 3, 1, 7, 0);
    Duration task2duration = Duration.ofMinutes(30);
    Task task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #2", "Task2 description", TaskStatus.NEW, task2StartDate, task2duration);
    LocalDateTime task3StartDate = LocalDateTime.of(2025, 3, 3, 7, 0);
    Duration task3duration = Duration.ofMinutes(30);
    Task task3 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #3", "Task3 description", TaskStatus.NEW, task3StartDate, task3duration);

    @BeforeEach
    void deleteGeneratedFile() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
    }

    /**
     * new file have to be created if we do not have existing
     **/
    @Test
    void loadFromFileWithNotExistingFileTest() {
        assertNotNull(FileBackedTaskManager.loadFromFile(FILE_PATH.toFile()));
    }

    /**
     * new file have to be created if we do not have any tasks in existing file
     **/
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

    /**
     * save file without tasks
     **/
    @Test
    void saveEmptyFileTest() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        LocalDateTime task1StartDate = LocalDateTime.of(2025, 3, 1, 8, 0);
        Duration task1duration = Duration.ofMinutes(30);

        Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW, task1StartDate, task1duration);
        int taskId1 = taskManager.addNewTask(task1);
        taskManager.deleteTask(taskId1);
        assertTrue(Files.exists(FILE_PATH));
    }

    /**
     * added to memory tasks exists in the file
     **/
    @Test
    void saveTasksToFileTest() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());

        int taskId1 = fileTaskManager.addNewTask(task1);

        List<String> lines = readLinesFromFile(FILE_PATH.toFile());
        assertTrue(lines.get(0).equals(FileConstants.CSV_HEADER));
        assertTrue(lines.get(1).substring(0, 3).equals(String.valueOf(taskId1)));
    }

    /**
     * it is possible to load empty file without lines
     **/
    @Test
    void loadNullFromFileTest() throws IOException {
        File file = new File(FILE_NAME);
        file.createNewFile();
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        assertNotNull(fileTaskManager);
    }

    /**
     * tasks cane be found in memory after loading from file
     **/
    @Test
    void loadTaskFromFile() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());

        int taskId1 = fileTaskManager.addNewTask(task1);
        fileTaskManager.updateTask(new Task(TaskStatus.DONE, fileTaskManager.getTask(taskId1)));
        FileBackedTaskManager loadedFromFileManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        assertTrue(loadedFromFileManager.findTaskById(taskId1).getId() == taskId1);
    }

    @Test
    void loadPrioritizedTasksFromFile() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        int taskId1 = fileTaskManager.addNewTask(task1);
        int taskId2 = fileTaskManager.addNewTask(task2);
        FileBackedTaskManager loadedFromFileManager = FileBackedTaskManager.loadFromFile(FILE_PATH.toFile());
        assertTrue(loadedFromFileManager.getPrioritizedTasks().getFirst().getStartTime()
                .isBefore(loadedFromFileManager.getPrioritizedTasks().getLast().getStartTime()));
    }

    /**
     * internal method to help with files reading
     **/
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