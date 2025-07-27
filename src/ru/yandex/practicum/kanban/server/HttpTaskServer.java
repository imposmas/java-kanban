package ru.yandex.practicum.kanban.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.managers.TasksManager;
import ru.yandex.practicum.kanban.server.handlers.*;
import ru.yandex.practicum.kanban.utils.ManagersUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final TasksManager manager;
    private HttpServer httpServer;


    public HttpTaskServer(TasksManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) throws IOException {

        TasksManager manager = ManagersUtils.getDefault();

        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubTasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
