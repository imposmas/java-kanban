package ru.yandex.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.exceptions.NotFoundException;
import ru.yandex.practicum.kanban.exceptions.OverlapException;
import ru.yandex.practicum.kanban.generics.TypeTokens.TaskTypeToken;
import ru.yandex.practicum.kanban.generics.tasks.Task;
import ru.yandex.practicum.kanban.managers.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {


    public TasksHandler(TasksManager tasksManager) {
        super(tasksManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK: {
                handleGetTask(exchange);
                break;
            }
            case CREATE_UPDATE_TASK: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                sendNotFound(exchange, "Эндпоинт не существует");
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_UPDATE_TASK;
            }
        }
        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(tasksManager.getTasks());
        sendText(exchange, response);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Некорректный идентификатор таски");
            return;
        }

        try {
            int taskId = taskIdOpt.get();
            Task task = tasksManager.getTask(taskId);
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, new TaskTypeToken().getType());
            if (task.getId() != 0) {
                tasksManager.updateTask(task);
            } else {
                try {
                    tasksManager.addNewTask(task);
                } catch (OverlapException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }
            }
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }

    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                sendNotFound(exchange, "Некорректный идентификатор таски");
                return;
            }

            int taskId = taskIdOpt.get();
            tasksManager.deleteTask(taskId);
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    enum Endpoint {GET_TASKS, GET_TASK, CREATE_UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
