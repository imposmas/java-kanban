package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.constants.FileConstants;
import ru.yandex.practicum.kanban.exceptions.FileSaveException;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.utils.HistoryReaderWriterHelper;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TasksManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager(Map<Integer, Task> tasks, Map<Integer, SubTask> subTasks, Map<Integer, Epic> epics,
                                 int uniqueId, File file, HistoryManager historyManager) {
        super(tasks, subTasks, epics, uniqueId, historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                lines.add(fileReader.readLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Будет создан новый");
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        if (lines.size() <= 1 || lines.subList(0, lines.size() - 2).size() <= 1) {
            System.out.println("В файле нет задач. Файл будет пересоздан");
            return new FileBackedTaskManager(file);
        } else {
            InMemoryTaskManager inMemoryTaskManager = loadStringsToInMemoryTaskManager(formatTasksLines(lines));
            HistoryManager historyManager = loadStringToInMemoryHistoryManager(HistoryReaderWriterHelper.fromStringToMemory(formatHistoryLines(lines)), inMemoryTaskManager);
            return new FileBackedTaskManager(inMemoryTaskManager.getALlTasks(),
                    inMemoryTaskManager.getALlSubTasks(),
                    inMemoryTaskManager.getALlEpics(), inMemoryTaskManager.getUniqueId(), file, historyManager);
        }
    }

    public static InMemoryTaskManager loadStringsToInMemoryTaskManager(List<String> lines) {
        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, SubTask> subTasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        int synchronizedId = 0;
        for (int i = 0; i < lines.size(); i++) {
            String[] context = lines.get(i).split(",");
            int id = Integer.parseInt(context[0]);

            if (id > synchronizedId) {
                synchronizedId = id;
            }

            String taskType = context[1];
            if (taskType.equals("TASK")) {
                Task task = new Task();
                task = task.fromCSVStringFormat(lines.get(i));
                if (!tasks.keySet().contains(task.getId())) {
                    tasks.put(task.getId(), task);
                }
            } else if (taskType.equals("SUBTASK")) {
                SubTask subTask = new SubTask();
                subTask = subTask.fromCSVStringFormat(lines.get(i));
                if (!subTasks.keySet().contains(subTask.getId())) {
                    subTasks.put(subTask.getId(), subTask);
                }
            } else if (taskType.equals("EPIC")) {
                Epic epic = new Epic();
                epic = epic.fromCSVStringFormat(lines.get(i));
                if (!epics.keySet().contains(epic.getId())) {
                    epics.put(epic.getId(), epic);
                }
            }
        }
        return new InMemoryTaskManager(tasks, subTasks, epics, synchronizedId);
    }

    public static HistoryManager loadStringToInMemoryHistoryManager(List<Integer> viewedTasksIds, InMemoryTaskManager inMemoryTaskManager) {
        if (viewedTasksIds != null) {
            HistoryManager inMemoryHistoryManager = ManagersUtils.getDefaultHistory();
            for (int i = 0; i < viewedTasksIds.size(); i++) {
                int taskId = viewedTasksIds.get(i);
                inMemoryHistoryManager.addToHistory(inMemoryTaskManager.findTaskById(taskId));
            }
            return inMemoryHistoryManager;
        } else {
            return new InMemoryHistoryManager();
        }
    }

    public static List<String> formatTasksLines(List<String> lines) {
        lines.removeFirst();
        return lines.subList(0, lines.size() - 3);
    }

    public static String formatHistoryLines(List<String> lines) {
        String historyLine = lines.get(lines.size() - 1);
        return historyLine.equals(FileConstants.HISTORY_HEADER) ? null : historyLine;
    }

    protected void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(FileConstants.CSV_HEADER);
            bufferedWriter.newLine();
            for (Task task : tasks.values()) {
                bufferedWriter.write(task.toCSVStringFormat());
                bufferedWriter.newLine();
            }
            for (Epic epic : epics.values()) {
                bufferedWriter.write(epic.toCSVStringFormat());
                bufferedWriter.newLine();
            }
            for (SubTask subtask : subTasks.values()) {
                bufferedWriter.write(subtask.toCSVStringFormat());
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();

            if (historyManager.getHistory() != null) {
                bufferedWriter.write(FileConstants.HISTORY_HEADER);
                bufferedWriter.newLine();
                bufferedWriter.write(HistoryReaderWriterHelper.fromMemoryToString(historyManager));
            }
        } catch (IOException exception) {
            throw new FileSaveException("Произошла ошибка во время записи файла.");
        }
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer addNewSubTask(SubTask subTask) {
        Integer subTaskID = super.addNewSubTask(subTask);
        save();
        return subTaskID;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

}
