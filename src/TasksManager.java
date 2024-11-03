import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private static int uniqueId = 0;

    public List<Task> getTasks(){
        ArrayList<Task> tasksArray = new ArrayList<>();
        for (Integer task : tasks.keySet()){
            tasksArray.add(tasks.get(task));
        }
        return tasksArray;
    };

    public List<SubTask> getSubTasks(){
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (Integer subTask : subTasks.keySet()){
            subTasksArray.add(subTasks.get(subTask));
        }
        return subTasksArray;
    };

    public List<Epic> getEpics(){
        ArrayList<Epic> epicArrayList = new ArrayList<>();
        for (Integer epic : epics.keySet()){
            epicArrayList.add(epics.get(epic));
        }
        return epicArrayList;
    };

    public List<SubTask> getEpicSubTasks(int epicId){
        ArrayList<SubTask> subTasksArray = new ArrayList<>();
        for (SubTask subtask : subTasks.values()){
            if(subtask.getEpicId() == epicId){
                subTasksArray.add(subtask);
            }
        }
        return subTasksArray;
    };

    public Task getTask(int id){
        return tasks.get(id);
    };

    public SubTask getSubTask(int id){
        return subTasks.get(id);
    };

    public Epic getEpic(int id){
        return epics.get(id);
    };

    public int addNewTask(Task task){
        uniqueId++;
        tasks.put(uniqueId,task);
        task.setId(uniqueId);
        return uniqueId;
    }

    public int addNewEpic(Epic epic){
        uniqueId++;
        epics.put(uniqueId, epic);
        epic.setId(uniqueId);
        calculateEpicStatus(epic);
        return uniqueId;
    };

    public Integer addNewSubTask(SubTask subTask){
        uniqueId++;
        subTasks.put(uniqueId, subTask);
        subTask.setId(uniqueId);
        return uniqueId;
    };

    public void updateTask(Task task) {
        tasks.replace(task.getId(),task);
    };

    public void updateEpic(Epic epic){
        epics.replace(epic.getId(), epic);
        calculateEpicStatus(epic);
    };

    public void updateSubTask(SubTask subTask){
        subTasks.replace(subTask.getId(), subTask);
        calculateEpicStatus(getEpic(subTask.getEpicId()));
    };

    public void deleteTask(int id){
        tasks.remove(id);
    };

    public void deleteEpic(int id){
        ArrayList<Integer> subTasksIdsForDelete = new ArrayList<>();
        for (SubTask subtask : subTasks.values()){
            if(subtask.getEpicId() == id){
                subTasksIdsForDelete.add(subtask.getId());
            }
        }
        for (Integer subTaskId : subTasksIdsForDelete){
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    };

    public void deleteSubTask(int id){
        subTasks.remove(id);
        int epicId = getSubTask(id).getEpicId();
        calculateEpicStatus(getEpic(epicId));
    };

    public void deleteTasks(){
        tasks.clear();
    };

    public void deleteSubTasks(){
        subTasks.clear();
    };

    public void deleteEpics(){
        subTasks.clear();
        epics.clear();
    };

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

}
