package com.yandex.kanban.servers.handles;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.model.HttpMethod;
import com.yandex.kanban.servers.HttpTaskServer;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;

public class HistoryHandle extends BaseHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandle(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (!HttpMethod.valueOf(method).equals(HttpMethod.GET)) {
                System.out.println("Такого эндпоинта не существует");
                writeResponse(exchange, "Такого эндпоинта не существует", 405);
                return;
            }
            final String response = gson.toJson(taskManager.history());
            writeResponse(exchange, response, 200);
        } catch (InternalError e) {
            writeResponse(exchange, "Произошла ошибка обработки запроса", 500);
        }
    }
}
