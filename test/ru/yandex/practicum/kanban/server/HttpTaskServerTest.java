package ru.yandex.practicum.kanban.server;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.adapters.DurationAdapter;
import ru.yandex.practicum.kanban.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.kanban.constants.TaskStatus;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TasksManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    // создаём экземпляр InMemoryTaskManager
    TasksManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        int taskId = manager.getTasks().getFirst().getId();
        Task updatedTask = new Task(TaskStatus.DONE, manager.getTask(taskId));
        String updatedTaskJson = gson.toJson(updatedTask);

        URI urlUpdate = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest requestToUpdate = HttpRequest.newBuilder().uri(urlUpdate).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();
        HttpResponse<String> responseToUpdate = client.send(requestToUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseToUpdate.statusCode());

        assertEquals(TaskStatus.DONE, manager.getTask(taskId).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);
        int taskId = manager.getTasks().getFirst().getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        manager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonElements = jsonElement.getAsJsonArray();
        ArrayList<Integer> tasksIds = new ArrayList<>();
        for (JsonElement jsonel : jsonElements) {
            JsonObject jsonObject = jsonel.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            tasksIds.add(id);
        }
        assertTrue(!tasksIds.isEmpty());
    }


    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);
        int taskId = manager.getTasks().getFirst().getId();

        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        manager.addNewTask(task2);
        int task2Id = manager.getTasks().getFirst().getId();
        Task taskResponse = manager.getTask(taskId);
        Task task2Response = manager.getTask(task2Id);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonElements = jsonElement.getAsJsonArray();
        ArrayList<Integer> tasksIds = new ArrayList<>();
        for (JsonElement jsonel : jsonElements) {
            JsonObject jsonObject = jsonel.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            tasksIds.add(id);
        }
        assertTrue(!tasksIds.isEmpty());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        manager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonElements = jsonElement.getAsJsonArray();
        ArrayList<Integer> tasksIds = new ArrayList<>();
        for (JsonElement jsonel : jsonElements) {
            JsonObject jsonObject = jsonel.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            tasksIds.add(id);
        }
        assertTrue(!tasksIds.isEmpty());
    }

    @Test
    public void testAddEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI urlEpic = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        int epicId = manager.getEpics().getFirst().getId();

        URI urlSubTask = URI.create("http://localhost:8080/subtasks/");
        SubTask subTask = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description",
                TaskStatus.NEW, epicId, LocalDateTime.now(), Duration.ofMinutes(5));
        String subTaskJson = gson.toJson(subTask);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubTask).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode());
        assertTrue(!manager.getEpicSubTasks(epicId).isEmpty());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("ru.yandex.practicum.kanban.generics.tasks.Epic #1", "Epic1 description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI urlEpic = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        int epicId = manager.getEpics().getFirst().getId();

        URI urlSubTask = URI.create("http://localhost:8080/subtasks/");
        SubTask subTask1 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description",
                TaskStatus.NEW, epicId, LocalDateTime.now(), Duration.ofMinutes(5));
        String subTaskJson1 = gson.toJson(subTask1);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubTask).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        SubTask subTask2 = new SubTask("ru.yandex.practicum.kanban.generics.tasks.SubTask #1-1", "SubTask1 description",
                TaskStatus.NEW, epicId, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        String subTaskJson2 = gson.toJson(subTask2);
        HttpRequest requestSubtask2 = HttpRequest.newBuilder().uri(urlSubTask).POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseSubtask.statusCode());
        assertEquals(201, responseSubtask2.statusCode());
        assertTrue(manager.getEpicSubTasks(epicId).size() == 2);

        URI urlEpicSubtasks = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest requestEpicSubtasks = HttpRequest.newBuilder().uri(urlEpicSubtasks).GET().build();
        HttpResponse<String> responseEpicSubtasks = client.send(requestEpicSubtasks, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(responseEpicSubtasks.body());
        JsonArray jsonElements = jsonElement.getAsJsonArray();
        ArrayList<Integer> subTasksIds = new ArrayList<>();
        for (JsonElement jsonel : jsonElements) {
            JsonObject jsonObject = jsonel.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            subTasksIds.add(id);
        }
        assertTrue(!subTasksIds.isEmpty());

    }

    @Test
    public void NotFoundExceptionTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void OverlapExceptionResponseTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        assertEquals("Overlapping exists between added and existing tasks", response.body());
    }

}