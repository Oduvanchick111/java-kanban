package com.yandex.kanban.servers.handles;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.servers.HttpTaskServer;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;

public class MainHandle extends BaseHandle implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public MainHandle(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/tasks(/\\d+)?")) {
                new TaskHandle(taskManager).handle(exchange);
            } else if (path.matches("/subtasks(/\\d+)?")) {
                new SubtaskHandle(taskManager).handle(exchange);
            } else if (path.matches("/epics(/\\d+)?") || path.matches("/epics(/\\d+)/(subtasks)")) {
                new EpicHandle(taskManager).handle(exchange);
            } else {
                switch (path) {
                    case "/prioritized":
                        new PrioritizedHandle(taskManager).handle(exchange);
                        break;
                    case "/history":
                        new HistoryHandle(taskManager).handle(exchange);
                        break;
                    default:
                        writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, "Ошибка, такого запроса не существует", 404);
        }
    }
}
