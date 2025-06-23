package ru.yandex.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.exceptions.NotFoundException;
import ru.yandex.practicum.kanban.exceptions.OverlapException;
import ru.yandex.practicum.kanban.generics.TypeTokens.EpicTypeToken;
import ru.yandex.practicum.kanban.generics.tasks.Epic;
import ru.yandex.practicum.kanban.managers.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TasksManager tasksManager) {
        super(tasksManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetEpicSubTasks(exchange);
                break;
            }
            case CREATE_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
            }
            default:
                sendNotFound(exchange, "Эндпоинт не существует");
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_EPIC;
            }
        }
        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        }
        if (pathParts.length == 4 && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(tasksManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Некорректный идентификатор таски");
            return;
        }
        try {
            int taskId = taskIdOpt.get();
            Epic epic = tasksManager.getEpic(taskId);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleGetEpicSubTasks(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Некорректный идентификатор таски");
            return;
        }
        int taskId = taskIdOpt.get();
        Epic epic = tasksManager.getEpic(taskId);
        if (epic != null) {
            String response = gson.toJson(tasksManager.getEpicSubTasks(taskId));
            sendText(exchange, response);
        } else {
            sendNotFound(exchange, "Эпика не существует");
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, new EpicTypeToken().getType());
            try {
                tasksManager.addNewEpic(epic);
            } catch (OverlapException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                sendNotFound(exchange, "Некорректный идентификатор таски");
                return;
            }

            int taskId = taskIdOpt.get();
            tasksManager.deleteEpic(taskId);
            sendCompleted(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    enum Endpoint { GET_EPICS, GET_EPIC, GET_EPIC_SUBTASKS, CREATE_EPIC, DELETE_EPIC, UNKNOWN }
}
