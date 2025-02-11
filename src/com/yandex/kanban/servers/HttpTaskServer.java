package com.yandex.kanban.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanban.exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskServer {
    public static final int PORT = 8080;
    private final Gson gson;
    private final TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
        server.createContext("/", this::mainHandle);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    private void mainHandle(HttpExchange exchange) throws IOException {
        try {
            System.out.println(exchange.getRequestURI());
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/tasks(/\\d+)?")) {
                handleTask(exchange);
            } else if (path.matches("/subtasks(/\\d+)?")) {
                subtaskHandle(exchange);
            } else if (path.matches("/epics(/\\d+)/subtasks?") || path.equals("/epics")) {
                epicHandle(exchange);
            } else {
                switch (path) {
                    case "/prioritized":
                        prioritizedHandle(exchange);
                        break;
                    case "/history":
                        historyHandle(exchange);
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

    private void handleTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        String response = "";
        int id;
        Task task;
        String[] splitQuery;
        switch (method) {
            case "GET":
                if (splitPath.length == 2) {
                    final List<Task> tasks = taskManager.getAllTasks();
                    response = gson.toJson(tasks);
                    System.out.println("Был запрос на получение всех задач");
                    writeResponse(exchange, response, 200);
                    return;
                } else if (splitPath.length == 3) {
                    id = Integer.parseInt(splitPath[2]);
                    task = taskManager.getTask(id);
                    response = gson.toJson(task);
                    System.out.println("Был запрос на получений задачи c id: " + id);
                    writeResponse(exchange, response, 200);
                }
                break;

            case "DELETE":
                if (splitPath.length == 2) {
                    taskManager.removeAllTasks();
                    System.out.println("Был запрос на удаление всех задач");
                    writeResponse(exchange, "были удалены все задачи", 201);
                } else {
                    id = Integer.parseInt(splitPath[2]);
                    taskManager.removeTaskById(id);
                    System.out.println("Удалили задачу с id: " + id);
                    writeResponse(exchange, "Удалили задачу с id: " + id, 201);
                }
                break;

            case "POST":
                String json = readText(exchange);
                if (json.isEmpty()) {
                    System.out.println("Передано пустое тело запроса");
                    writeResponse(exchange, "Передано пустое тело запроса", 400);
                    return;
                }
                try {
                    task = gson.fromJson(json, Task.class);
                    id = task.getId();
                    if (id != 0) {
                        taskManager.updateTask(task);
                        System.out.println("Был запрос на обновление задачи");
                        writeResponse(exchange, "Задача обновлена", 201);
                    } else {
                        taskManager.createTask(task);
                        System.out.println("Был запрос на создании задачи");
                        System.out.println(task.getId());
                        writeResponse(exchange, "Задача добавлена", 201);
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("JsonSyntaxException");
                } catch (ValidateException e) {
                    System.out.println("ValidateException");
                }
                break;
        }
    }

    private void epicHandle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        String response = "";
        int id;
        Epic epic;
        String[] splitQuery;
        switch (method) {
            case "GET":
                if (splitPath.length == 2) {
                    final List<Epic> tasks = taskManager.getAllEpics();
                    response = gson.toJson(tasks);
                    System.out.println("Был запрос на получение всех задач");
                    writeResponse(exchange, response, 200);
                } else if (splitPath.length == 3) {
                    id = Integer.parseInt(splitPath[1]);
                    epic = taskManager.getEpic(id);
                    response = gson.toJson(epic);
                    System.out.println("Был запрос на получений задачи c id: " + id);
                    writeResponse(exchange, response, 200);
                } else if (splitPath.length == 4) {
                    id = Integer.parseInt(splitPath[1]);
                    epic = taskManager.getEpic(id);
                    ArrayList<Subtask> subtasks = new ArrayList<>();
                    for (Integer subtaskId : epic.getSubtasksId()) {
                        subtasks.add(taskManager.getSubtask(subtaskId));
                    }
                    response = subtasks.toString();
                    writeResponse(exchange, response, 200);
                }
                break;

            case "DELETE":
                if (splitPath.length == 2) {
                    taskManager.removeAllEpics();
                    System.out.println("Был запрос на удаление всех задач");
                    writeResponse(exchange, "были удалены все задачи", 201);
                } else {
                    id = Integer.parseInt(splitPath[2]);
                    taskManager.removeEpicById(id);
                    System.out.println("Удалили задачу с id: " + id);
                    writeResponse(exchange, "Удалили задачу с id: " + id, 201);
                }
                break;

            case "POST":
                String json = readText(exchange);
                if (json.isEmpty()) {
                    System.out.println("Передано пустое тело запроса");
                    writeResponse(exchange, "Передано пустое тело запроса", 400);
                    return;
                }
                epic = gson.fromJson(json, Epic.class);
                id = epic.getId();
                if (id != 0) {
                    taskManager.updateTask(epic);
                    System.out.println("Был запрос на обновление задачи");
                    writeResponse(exchange, "Задача обновлена", 201);
                } else {
                    try {
                        taskManager.createTask(epic);
                        System.out.println("Был запрос на создании задачи");
                        writeResponse(exchange, "Задача добавлена", 201);
                    } catch (ValidateException e) {
                        System.out.println("Был пойман Validate Exception");
                        writeResponse(exchange, "Невозможно добавить задачу в заданный временной промежуток", 406);
                    }
                }
                break;

        }
    }

    private void subtaskHandle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        String response = "";
        int id;
        Subtask subtask;
        switch (method) {
            case "GET":
                if (splitPath.length == 2) {
                    final List<Subtask> subtasks = taskManager.getAllSubtasks();
                    response = gson.toJson(subtasks);
                    System.out.println("Был запрос на получение всех задач");
                    writeResponse(exchange, response, 200);
                } else {
                    id = Integer.parseInt(splitPath[2]);
                    subtask = taskManager.getSubtask(id);
                    response = gson.toJson(subtask);
                    System.out.println("Был запрос на получений задачи c id: " + id);
                    writeResponse(exchange, response, 200);
                }
                break;

            case "DELETE":
                if (splitPath.length == 2) {
                    taskManager.removeAllTasks();
                    System.out.println("Был запрос на удаление всех задач");
                    writeResponse(exchange, "были удалены все задачи", 201);
                } else {
                    id = Integer.parseInt(splitPath[2]);
                    taskManager.removeSubtaskById(id);
                    System.out.println("Удалили задачу с id: " + id);
                    writeResponse(exchange, "Удалили задачу с id: " + id, 201);
                }
                break;

            case "POST":
                String json = readText(exchange);
                if (json.isEmpty()) {
                    System.out.println("Передано пустое тело запроса");
                    writeResponse(exchange, "Передано пустое тело запроса", 400);
                    return;
                }
                subtask = gson.fromJson(json, Subtask.class);
                id = subtask.getId();
                if (id != 0) {
                    taskManager.updateTask(subtask);
                    System.out.println("Был запрос на обновление задачи");
                    writeResponse(exchange, "Задача обновлена", 201);
                } else {
                    try {
                        taskManager.createTask(subtask);
                        System.out.println("Был запрос на создании задачи");
                        writeResponse(exchange, "Задача добавлена", 201);
                    } catch (ValidateException e) {
                        System.out.println("Был пойман Validate Exception");
                        writeResponse(exchange, "Невозможно добавить задачу в заданный временной промежуток", 406);
                    }
                }
                break;
        }
    }

    private void historyHandle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!method.equals("GET")) {
            System.out.println("Такого эндпоинта не существует");
            writeResponse(exchange, "Такого эндпоинта не существует", 405);
            return;
        }
        final String response = gson.toJson(taskManager.history());
        writeResponse(exchange, response, 200);
    }

    private void prioritizedHandle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!method.equals("GET")) {
            System.out.println("Такого эндпоинта не существует");
            writeResponse(exchange, "Такого эндпоинта не существует", 405);
            return;
        }
        final String response = gson.toJson(taskManager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop(int time) {
        System.out.println("Останавливаем работу сервера на порту: " + PORT);
        server.stop(time);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();


//        httpTaskServer.stop(1);
    }
}




