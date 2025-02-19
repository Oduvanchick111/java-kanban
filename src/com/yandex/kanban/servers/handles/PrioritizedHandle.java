package com.yandex.kanban.servers.handles;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.model.HttpMethod;
import com.yandex.kanban.servers.HttpTaskServer;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandle extends BaseHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandle(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (!HttpMethod.valueOf(method).equals(HttpMethod.GET)) {
                String request = "Такого эндпоинта не существует";
                System.out.println(request);
                writeResponse(exchange, request, 405);
                return;
            }
            final String response = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(exchange, response, 200);
        } catch (InternalError e) {
            writeResponse(exchange, "Произошла ошибка обработки запроса", 500);
        }
    }
}
