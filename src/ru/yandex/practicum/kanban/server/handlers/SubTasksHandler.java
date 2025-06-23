package ru.yandex.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.exceptions.NotFoundException;
import ru.yandex.practicum.kanban.exceptions.OverlapException;
import ru.yandex.practicum.kanban.generics.TypeTokens.SubTaskTypeToken;
import ru.yandex.practicum.kanban.generics.tasks.SubTask;
import ru.yandex.practicum.kanban.managers.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubTasksHandler(TasksManager tasksManager) {
        super(tasksManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubTasks(exchange);
                break;
            }
            case GET_SUBTASK: {
                handleGetSubTask(exchange);
                break;
            }
            case CREATE_UPDATE_SUBTASK: {
                handlePostSubTasks(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubTask(exchange);
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
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_UPDATE_SUBTASK;
            }
        }
        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(tasksManager.getSubTasks());
        sendText(exchange, response);
    }

    private void handleGetSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Некорректный идентификатор таски");
            return;
        }
        try {
            int taskId = taskIdOpt.get();
            SubTask subTask = tasksManager.getSubTask(taskId);
            String response = gson.toJson(subTask);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostSubTasks(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            SubTask subTaskask = gson.fromJson(body, new SubTaskTypeToken().getType());
            if (subTaskask.getId() != 0) {
                tasksManager.updateSubTask(subTaskask);
            } else {
                try {
                    tasksManager.addNewSubTask(subTaskask);
                } catch (OverlapException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }
            }
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                sendNotFound(exchange, "Некорректный идентификатор таски");
                return;
            }

            int taskId = taskIdOpt.get();
            tasksManager.deleteSubTask(taskId);
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    enum Endpoint {GET_SUBTASKS, GET_SUBTASK, CREATE_UPDATE_SUBTASK, DELETE_SUBTASK, UNKNOWN}
}
