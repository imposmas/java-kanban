package ru.yandex.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.managers.TasksManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TasksManager tasksManager) {
        super(tasksManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("GET")) {
            sendText(exchange, gson.toJson(tasksManager.getHistoryManager().getHistory()));
        } else {
            sendNotFound(exchange, "Ресурс не найден");
        }
    }
}
