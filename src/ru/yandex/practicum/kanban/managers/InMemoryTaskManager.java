package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager<T extends Task> implements TasksManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private static int uniqueId = 0;
    HistoryManager historyManager = ManagersUtils.getDefaultHistory();


    @Override
    public List<Task> getTasks() {
        ArrayList<Task> tasksArray = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            tasksArray.add(tasks.get(task));
        }
        return tasksArray;
    }

    @Override
    public List<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (Integer subTask : subTasks.keySet()) {
            subTasksArray.add(subTasks.get(subTask));
        }
        return subTasksArray;
    }

    @Override
    public List<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>();
        for (Integer epic : epics.keySet()) {
            epicArrayList.add(epics.get(epic));
        }
        return epicArrayList;
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subTasksArray.add(subtask);
            }
        }
        return subTasksArray;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (isNotNull(task)) {
            historyManager.addToHistory(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (isNotNull(subTask)) {
            historyManager.addToHistory(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (isNotNull(epic)) {
            historyManager.addToHistory(epic);
        }
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        int id = generateUniqueId();
        tasks.put(id, new Task(id, task));
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = generateUniqueId();
        Epic newEpic = new Epic(id, epic);
        epics.put(id, newEpic);
        calculateEpicStatus(newEpic);
        return id;
    }

    @Override
    public Integer addNewSubTask(SubTask subTask) {
        if (getEpic(subTask.getEpicId()) != null) {
            if (getEpic(subTask.getEpicId()) instanceof Epic) {
                int id = generateUniqueId();
                SubTask newSubtask = new SubTask(id, subTask);
                subTasks.put(id, newSubtask);
                getEpic(subTask.getEpicId()).getSubTasks().add(id);
                calculateEpicStatus(getEpic(subTask.getEpicId()));
                return id;
            }
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
        calculateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (getEpic(subTask.getEpicId()) != null) {
            if (getEpic(subTask.getEpicId()) instanceof Epic) {
                subTasks.replace(subTask.getId(), subTask);
                calculateEpicStatus(getEpic(subTask.getEpicId()));
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        ArrayList<SubTask> subtaskToCleanUp = new ArrayList<>(subTasks.values()
                .stream().filter(subTask -> subTask.getEpicId() == id).toList());
        for (SubTask subTask : subtaskToCleanUp) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        //subTasks.values().removeIf(subtask -> subtask.getEpicId() == id);
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        int epicId = getSubTask(id).getEpicId();
        if (getEpics().contains(getEpic(epicId))) {
            historyManager.remove(id);
            subTasks.remove(id);
            calculateEpicStatus(getEpic(epicId));
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    private void calculateEpicStatus(Epic epic) {
        ArrayList<SubTask> subTasksPerEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epic.getId()) {
                subTasksPerEpic.add(subTask);
            }
        }
        if (subTasksPerEpic.isEmpty() || isAllSubTasksNew(subTasksPerEpic)) {
            //epic.setStatus(TaskStatus.NEW);
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.NEW));
        } else if (isAllSubTasksDone(subTasksPerEpic)) {
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.DONE));
        } else {
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.IN_PROGRESS));
        }
    }

    public boolean isAllSubTasksNew(ArrayList<SubTask> subTasks) {
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.NEW));
    }

    public boolean isAllSubTasksDone(ArrayList<SubTask> subTasks) {
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.DONE));
    }

    private int generateUniqueId() {
        return ++uniqueId;
    }

    private boolean isNotNull(Task task) {
        return task != null;
    }

}
