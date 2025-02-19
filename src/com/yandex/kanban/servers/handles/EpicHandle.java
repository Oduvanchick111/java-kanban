package com.yandex.kanban.servers.handles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanban.exceptions.NotFoundException;
import com.yandex.kanban.exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.HttpMethod;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.servers.HttpTaskServer;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpicHandle extends BaseHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandle(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");
            String response = "";
            int id;
            switch (HttpMethod.valueOf(method)) {
                case GET:
                    if (splitPath.length == 2) {
                        System.out.println("Длинна массива с данными сплитпаф = " + splitPath.length);
                        final List<Epic> tasks = taskManager.getAllEpics();
                        response = gson.toJson(tasks);
                        System.out.println("Был запрос на получение всех задач");
                        writeResponse(exchange, response, 200);
                    } else if (splitPath.length == 3) {
                        try {
                            System.out.println("Длинна массива с данными сплитпаф = " + splitPath.length);
                            id = Integer.parseInt(splitPath[2]);
                            Epic epic = taskManager.getEpic(id);
                            response = gson.toJson(epic);
                            System.out.println("Был запрос на получений задачи c id: " + id);
                            writeResponse(exchange, response, 200);
                        } catch (NotFoundException e) {
                            writeResponse(exchange, "Задачи с таким id не существует", 404);
                        }

                    } else if (splitPath.length == 4) {
                        try {
                            System.out.println("Длинна массива с данными сплитпаф = " + splitPath.length);
                            id = Integer.parseInt(splitPath[2]);
                            Epic epic = taskManager.getEpic(id);
                            ArrayList<Subtask> subtasks = new ArrayList<>();
                            for (Integer subtaskId : epic.getSubtasksId()) {
                                subtasks.add(taskManager.getSubtask(subtaskId));
                            }
                            response = gson.toJson(subtasks);
                            writeResponse(exchange, response, 200);
                        } catch (NotFoundException e) {
                            writeResponse(exchange, "Задачи с таким id не существует", 404);
                        }
                    }
                    break;

                case DELETE:
                    if (splitPath.length == 3) {
                        try {
                            id = Integer.parseInt(splitPath[2]);
                            taskManager.removeEpicById(id);
                            System.out.println("Удалили задачу с id: " + id);
                            writeResponse(exchange, "Удалили задачу с id: " + id, 201);
                        } catch (NotFoundException e) {
                            writeResponse(exchange, "Задачи с таким id не существует", 404);
                        }
                    } else {
                        writeResponse(exchange, "Такого эндпоинта не существует", 406);
                    }
                    break;

                case POST:
                    String json = readText(exchange);
                    if (json.isEmpty()) {
                        System.out.println("Передано пустое тело запроса");
                        writeResponse(exchange, "Передано пустое тело запроса", 400);
                        return;
                    }
                    try {
                        Epic epic = gson.fromJson(json, Epic.class);
                        id = epic.getId();
                        if (id != 0 && taskManager.getAllEpics().contains(epic)) {
                            taskManager.updateEpic(epic);
                            System.out.println("Был запрос на обновление задачи");
                            writeResponse(exchange, "Задача обновлена", 201);
                        } else if (id == 0) {
                            taskManager.createEpic(epic);
                            writeResponse(exchange, "Задача добавлена", 201);
                        } else {
                            writeResponse(exchange, "Такой задачи не существует", 404);
                            throw new NotFoundException("Такой задачи нет");
                        }
                    } catch (JsonSyntaxException e) {
                        System.out.println("JsonSyntaxException");
                        writeResponse(exchange, "Не удалось преобразовать объект Json", 406);
                    } catch (ValidateException e) {
                        System.out.println("ValidateException");
                        writeResponse(exchange, "Данная задача пересекается с предыдущей", 406);
                    }
                    break;
            }
        } catch (InternalError e) {
            writeResponse(exchange, "Произошла ошибка обработки запроса", 500);
        }
    }
}
