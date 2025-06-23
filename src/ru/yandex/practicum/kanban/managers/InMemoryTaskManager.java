package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.exceptions.NotFoundException;
import ru.yandex.practicum.kanban.exceptions.OverlapException;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TasksManager {

    private static int uniqueId = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    protected HistoryManager historyManager = ManagersUtils.getDefaultHistory();

    public InMemoryTaskManager() {
    }

    public InMemoryTaskManager(Map<Integer, Task> tasks, Map<Integer, SubTask> subTasks,
                               Map<Integer, Epic> epics, int uniqueId, HistoryManager historyManager,
                               TreeSet<Task> prioritizedTasks) {
        this.tasks = tasks;
        this.subTasks = subTasks;
        this.epics = epics;
        this.uniqueId = uniqueId;
        this.historyManager = historyManager;
        this.prioritizedTasks = prioritizedTasks;
    }

    public InMemoryTaskManager(Map<Integer, Task> tasks, Map<Integer, SubTask> subTasks, Map<Integer, Epic> epics, int uniqueId) {
        this.tasks = tasks;
        this.subTasks = subTasks;
        this.epics = epics;
        this.uniqueId = uniqueId;
    }

    public Map<Integer, Task> getALlTasks() {
        return tasks;
    }

    public Map<Integer, SubTask> getALlSubTasks() {
        return subTasks;
    }

    public Map<Integer, Epic> getALlEpics() {
        return epics;
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<SubTask> getSubTasks() {
        return subTasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .toList();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (isNotNull(task)) {
            historyManager.addToHistory(task);
        } else {
            throw new NotFoundException("Таска не существует");
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (isNotNull(subTask)) {
            historyManager.addToHistory(subTask);
        } else {
            throw new NotFoundException("Сабтаска не существует");
        }
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (isNotNull(epic)) {
            historyManager.addToHistory(epic);
        } else {
            throw new NotFoundException("Эпика не существует");
        }
        return epic;
    }

    public Task findTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }

        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    @Override
    public int addNewTask(Task task) {
        int id = generateUniqueId();
        Task taskNew = new Task(id, task);
        overlapWithExistingTasksCheck(taskNew);
        tasks.put(id, taskNew);
        prioritizedTasks.add(taskNew);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = generateUniqueId();
        Epic newEpic = new Epic(id, epic);
        epics.put(id, newEpic);
        calculateEpicStatus(newEpic);
        calculateEpicStartTimeAndDuration(epics.get(id));
        return id;
    }

    @Override
    public Integer addNewSubTask(SubTask subTask) {
        if (getEpic(subTask.getEpicId()) != null) {
            if (getEpic(subTask.getEpicId()) instanceof Epic) {
                int id = generateUniqueId();
                SubTask newSubtask = new SubTask(id, subTask);
                overlapWithExistingTasksCheck(newSubtask);
                subTasks.put(id, newSubtask);
                prioritizedTasks.add(newSubtask);
                //getEpic(subTask.getEpicId()).getSubTasks().add(id);
                Epic epic = getEpic(subTask.getEpicId());
                epic.getSubTasks().add(id);
                epics.replace(subTask.getEpicId(), epic);
                calculateEpicStatus(getEpic(subTask.getEpicId()));
                calculateEpicStartTimeAndDuration(getEpic(subTask.getEpicId()));
                return id;
            }
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        overlapWithExistingTasksCheck(task);
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.replace(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
        calculateEpicStatus(epic);
        calculateEpicStartTimeAndDuration(epics.get(epic.getId()));
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (getEpic(subTask.getEpicId()) != null) {
            if (getEpic(subTask.getEpicId()) instanceof Epic) {
                overlapWithExistingTasksCheck(subTask);
                prioritizedTasks.remove(subTasks.get(subTask.getId()));
                subTasks.replace(subTask.getId(), subTask);
                calculateEpicStatus(getEpic(subTask.getEpicId()));
                calculateEpicStartTimeAndDuration(getEpic(subTask.getEpicId()));
                prioritizedTasks.add(subTask);
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        ArrayList<SubTask> subtaskToCleanUp = new ArrayList<>(subTasks.values()
                .stream().filter(subTask -> subTask.getEpicId() == id).toList());
        for (SubTask subTask : subtaskToCleanUp) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTasks.get(subTask.getId()));
            subTasks.remove(subTask.getId());
        }
        //subTasks.values().removeIf(subtask -> subtask.getEpicId() == id);
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        int epicId = getSubTask(id).getEpicId();
        if (getEpics().contains(getEpic(epicId))) {
            Epic epic = getEpic(epicId);
            epic.getSubTasks().remove(epic.getSubTasks().indexOf(id));
            epics.replace(epicId, epic);
            historyManager.remove(id);
            ;
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
            calculateEpicStatus(getEpic(epicId));
            calculateEpicStartTimeAndDuration(getEpic(epicId));
        }
    }

    @Override
    public void deleteTasks() {
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        prioritizedTasks.removeAll(subTasks.values());
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        prioritizedTasks.removeAll(subTasks.values());
        subTasks.clear();
        epics.clear();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void calculateEpicStatus(Epic epic) {
        List<SubTask> subTasksPerEpic = new ArrayList<>(epic.getSubTasks().stream()
                .map(subTask -> subTasks.get(subTask)).toList());

        if (subTasksPerEpic.isEmpty() || isAllSubTasksNew(subTasksPerEpic)) {
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.NEW));
        } else if (isAllSubTasksDone(subTasksPerEpic)) {
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.DONE));
        } else {
            epics.replace(epic.getId(), new Epic(epic, TaskStatus.IN_PROGRESS));
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected void calculateEpicStartTimeAndDuration(Epic epic) {
        List<SubTask> subTasksPerEpic = new ArrayList<>(epic.getSubTasks().stream()
                .map(subTask -> subTasks.get(subTask)).toList());

        Duration epicDuration = Duration.ofMinutes(0);
        if (!subTasksPerEpic.isEmpty()) {
            subTasksPerEpic.sort(taskComparator);
            LocalDateTime epicStartTime = subTasksPerEpic.getFirst().getStartTime();

            for (int i = 0; i < subTasksPerEpic.size(); i++) {
                epicDuration = epicDuration.plusMinutes(subTasksPerEpic.get(i).getTaskDuration().toMinutes());
            }
            epics.replace(epic.getId(), new Epic(epic, epicStartTime, epicDuration));
        } else {
            epics.replace(epic.getId(), new Epic(epic, null, epicDuration));
        }

    }

    public boolean isAllSubTasksNew(List<SubTask> subTasks) {
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.NEW));
    }

    public boolean isAllSubTasksDone(List<SubTask> subTasks) {
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.DONE));
    }

    private int generateUniqueId() {
        return ++uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    private boolean isNotNull(Task task) {
        return task != null;
    }

    private void overlapWithExistingTasksCheck(Task task) throws OverlapException {
        TreeSet<Task> existingTasks = getPrioritizedTasks();

        Optional<Task> overlapTask = existingTasks.stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .filter(existingTask -> Objects.nonNull(existingTask.getStartTime()) && Objects.nonNull(task.getStartTime()))
                .filter(existingTask -> !isNotOverlap(existingTask, task))
                .findFirst();

        if (overlapTask.isPresent()) {
            throw new OverlapException("Overlapping exists between added and existing tasks");
        }
    }

    private boolean isNotOverlap(Task existingTask, Task task) {
        return existingTask.getStartTime().isAfter(task.getEndTime()) || existingTask.getEndTime().isBefore(task.getStartTime());
    }
}
