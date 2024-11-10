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
    HistoryManager<Task> historyManager = ManagersUtils.getDefaultHistory();


    @Override
    public List<Task> getTasks(){
        ArrayList<Task> tasksArray = new ArrayList<>();
        for (Integer task : tasks.keySet()){
            tasksArray.add(tasks.get(task));
        }
        return tasksArray;
    }

    @Override
    public List<SubTask> getSubTasks(){
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (Integer subTask : subTasks.keySet()){
            subTasksArray.add(subTasks.get(subTask));
        }
        return subTasksArray;
    }

    @Override
    public List<Epic> getEpics(){
        ArrayList<Epic> epicArrayList = new ArrayList<>();
        for (Integer epic : epics.keySet()){
            epicArrayList.add(epics.get(epic));
        }
        return epicArrayList;
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId){
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (SubTask subtask : subTasks.values()){
            if(subtask.getEpicId() == epicId){
                subTasksArray.add(subtask);
            }
        }
        return subTasksArray;
    }

    @Override
    public Task getTask(int id){
        historyManager.addToHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id){
        historyManager.addToHistory(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id){
        historyManager.addToHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public int addNewTask(Task task){
        int id = generateUniqueId();
        tasks.put(id,task);
        task.setId(id);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic){
        int id = generateUniqueId();
        epics.put(id, epic);
        epic.setId(id);
        calculateEpicStatus(epic);
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask){
        int id = generateUniqueId();
        subTasks.put(id, subTask);
        subTask.setId(id);
        getEpic(subTask.getEpicId()).getSubTasks().add(id);
        calculateEpicStatus(getEpic(subTask.getEpicId()));
        return id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(),task);
    }

    @Override
    public void updateEpic(Epic epic){
        epics.replace(epic.getId(), epic);
        calculateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask){
        subTasks.replace(subTask.getId(), subTask);
        calculateEpicStatus(getEpic(subTask.getEpicId()));
    }

    @Override
    public void deleteTask(int id){
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id){
        subTasks.values().removeIf(subtask -> subtask.getEpicId() == id);
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id){
        int epicId = getSubTask(id).getEpicId();
        subTasks.remove(id);
        calculateEpicStatus(getEpic(epicId));
    }

    @Override
    public void deleteTasks(){
        tasks.clear();
    }

    @Override
    public void deleteSubTasks(){
        subTasks.clear();
    }

    @Override
    public void deleteEpics(){
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void calculateEpicStatus(Epic epic){
        ArrayList<SubTask> subTasksPerEpic = new ArrayList<>();
        for(SubTask subTask : subTasks.values()){
            if(subTask.getEpicId() == epic.getId()){
                subTasksPerEpic.add(subTask);
            }
        }
        if (subTasksPerEpic.isEmpty() || isAllSubTasksNew(subTasksPerEpic)){
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllSubTasksDone(subTasksPerEpic)){
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public boolean isAllSubTasksNew(ArrayList<SubTask> subTasks){
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.NEW));
    }

    public boolean isAllSubTasksDone(ArrayList<SubTask> subTasks){
        return subTasks.stream().allMatch(subTask -> subTask.getStatus().equals(TaskStatus.DONE));
    }

    @Override
    public int generateUniqueId(){
        return ++uniqueId;
    }

}
