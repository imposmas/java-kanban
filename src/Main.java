import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.managers.TasksManager;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

public class Main {
    public static void main(String[] args) {

        TasksManager manager = ManagersUtils.getDefault();

        // Создание
        Task task1 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("ru.yandex.practicum.kanban.generics.tasks.Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        final int taskId1 = manager.addNewTask(task1);
        final int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        Epic epic2 = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #2", "Epic2 description");
        final int epicId1 = manager.addNewEpic(epic1);
        final int epicId2 = manager.addNewEpic(epic2);

        SubTask subTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTask subTask2 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #2-1", "SubTask1 description", TaskStatus.NEW, epicId1);
        SubTask subTask3 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #3-2", "SubTask1 description", TaskStatus.DONE, epicId2);
        final int subTaskId1 = manager.addNewSubTask(subTask1);
        final int subTaskId2 = manager.addNewSubTask(subTask2);
        final int subTaskId3 = manager.addNewSubTask(subTask3);

        printAllTasks(manager);

        // Обновление
        final Task task = manager.getTask(taskId2);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);
        System.out.println("CHANGE STATUS: Task2 IN_PROGRESS->DONE");
        System.out.println("Задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        SubTask subTask = manager.getSubTask(subTaskId2);
        subTask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask);
        System.out.println("CHANGE STATUS: SubTask2 NEW->DONE");
        subTask = manager.getSubTask(subTaskId3);
        subTask.setStatus(TaskStatus.NEW);
        manager.updateSubTask(subTask);
        System.out.println("CHANGE STATUS: SubTask3 DONE->NEW");
        System.out.println("Подзадачи:");
        for (Task t : manager.getSubTasks()) {
            System.out.println(t);
        }

        System.out.println("Эпики:");
        for (Task e : manager.getEpics()) {
            System.out.println(e);
            for (Task t : manager.getEpicSubTasks(e.getId())) {
                System.out.println("--> " + t);
            }
        }
        final Epic epic = manager.getEpic(epicId1);
        epic.setStatus(TaskStatus.NEW);
        manager.updateEpic(epic);
        System.out.println("CHANGE STATUS: Epic1 IN_PROGRESS->NEW");
        printAllTasks(manager);

        System.out.println("Эпики:");
        for (Task e : manager.getEpics()) {
            System.out.println(e);
            for (Task t : manager.getEpicSubTasks(e.getId())) {
                System.out.println("--> " + t);
            }
        }

        // Удаление
        System.out.println("DELETE: Task1");
        manager.deleteTask(taskId1);
        System.out.println("DELETE: Epic2");
        manager.deleteEpic(epicId2);
        System.out.println("DELETE: SubTask2");
        manager.deleteSubTask(subTaskId2);
        printAllTasks(manager);
    }

    static void printAllTasks(TasksManager tasksManager) {
        for (Task t : tasksManager.getTasks()) {
            System.out.println(t);
        }
        for (Task t : tasksManager.getSubTasks()) {
            System.out.println(t);
        }
        for (Task t : tasksManager.getEpics()) {
            System.out.println(t);
        }

    }
}