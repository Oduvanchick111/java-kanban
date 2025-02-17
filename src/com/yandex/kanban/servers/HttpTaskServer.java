package com.yandex.kanban.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanban.exceptions.NotFoundException;
import com.yandex.kanban.exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.HttpMethod;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.servers.settings.DurationAdapter;
import com.yandex.kanban.servers.settings.LocalDateTimeAdapter;
import com.yandex.kanban.service.Managers;
import com.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskServer {
    public static final int PORT = 8080;
    private final Gson gson;
    private final TaskManager taskManager;
    private final HttpServer server;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
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
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/tasks(/\\d+)?")) {
                handleTask(exchange);
            } else if (path.matches("/subtasks(/\\d+)?")) {
                subtaskHandle(exchange);
            } else if (path.matches("/epics(/\\d+)?") || path.matches("/epics(/\\d+)/(subtasks)")) {
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
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");
            String response = "";
            int id;
            Task task;
            String[] splitQuery;
            switch (HttpMethod.valueOf(method)) {
                case GET:
                    if (splitPath.length == 2) {
                        final List<Task> tasks = taskManager.getAllTasks();
                        response = gson.toJson(tasks);
                        System.out.println("Был запрос на получение всех задач");
                        writeResponse(exchange, response, 200);
                        return;
                    } else if (splitPath.length == 3) {
                        try {
                            id = Integer.parseInt(splitPath[2]);
                            task = taskManager.getTask(id);
                            response = gson.toJson(task);
                            System.out.println("Был запрос на получений задачи c id: " + id);
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
                            taskManager.removeTaskById(id);
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
                        task = gson.fromJson(json, Task.class);
                        id = task.getId();
                        if (id != 0) {
                            taskManager.updateTask(task);
                            System.out.println("Был запрос на обновление задачи");
                            writeResponse(exchange, "Задача обновлена", 201);
                        } else {
                            taskManager.createTask(task);
                            System.out.println("Был запрос на создание задачи");
                            writeResponse(exchange, "Задача добавлена", 201);
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

    private void epicHandle(HttpExchange exchange) throws IOException {
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

    private void subtaskHandle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");
            String response = "";
            int id;
            Subtask subtask;
            switch (HttpMethod.valueOf(method)) {
                case GET:
                    if (splitPath.length == 2) {
                        final List<Subtask> subtasks = taskManager.getAllSubtasks();
                        response = gson.toJson(subtasks);
                        System.out.println("Был запрос на получение всех задач");
                        writeResponse(exchange, response, 200);
                    } else {
                        try {
                            id = Integer.parseInt(splitPath[2]);
                            subtask = taskManager.getSubtask(id);
                            response = gson.toJson(subtask);
                            System.out.println("Был запрос на получений задачи c id: " + id);
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
                            taskManager.removeSubtaskById(id);
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
                        subtask = gson.fromJson(json, Subtask.class);
                        id = subtask.getId();
                        if (id != 0) {
                            taskManager.updateSubtask(subtask);
                            System.out.println("Был запрос на обновление задачи");
                            writeResponse(exchange, "Задача обновлена", 201);
                        } else {
                            taskManager.createSubtask(subtask);
                            System.out.println("Был запрос на создание задачи");
                            writeResponse(exchange, "Задача добавлена", 201);
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

    private void historyHandle(HttpExchange exchange) throws IOException {
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

    private void prioritizedHandle(HttpExchange exchange) throws IOException {
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

    private void writeResponse(HttpExchange exchange,
                               String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes());
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем работу сервера на порту: " + PORT);
        server.stop(0);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes());
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();

    }
}




