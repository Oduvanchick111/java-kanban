package com.yandex.kanban.server;

import com.google.gson.Gson;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Status;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.servers.HttpTaskServer;
import com.yandex.kanban.servers.settings.SubtasksListTypeToken;
import com.yandex.kanban.servers.settings.TasksListTypeToken;
import com.yandex.kanban.service.InMemoryTaskManager;
import com.yandex.kanban.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void addTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void addEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Описание эпика", Status.NEW);
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void addSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Subtask", "Описание сабтаска", 1);
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI urlEpic = URI.create("http://localhost:8080/epics");
        URI urlSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void getTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Таск1", "Описание");
        manager.createTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetTaskId = URI.create("http://localhost:8080/tasks/1");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(urlGetTaskId)
                .GET().build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(task, returnedTask);
    }

    @Test
    public void getEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание");
        manager.createEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetEpicId = URI.create("http://localhost:8080/epics/1");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(urlGetEpicId)
                .GET().build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(epic, returnedEpic);
    }

    @Test
    public void getSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Описание", epic.getId());
        manager.createSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI urlGetSubtasksId = URI.create("http://localhost:8080/subtasks/2");
        System.out.println(epic);
        System.out.println(subtask);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(urlGetSubtasksId)
                .GET().build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Subtask returnedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEquals(subtask, returnedSubtask);
    }

    @Test
    public void validateExceptionTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", Status.NEW, LocalDateTime.of(2015, 10, 25, 13, 20), Duration.ofMinutes(60));
        Task task2 = new Task("Task2", "Testing task 2", Status.NEW, LocalDateTime.of(2015, 10, 25, 13, 30), Duration.ofMinutes(60));
        String task2Json = gson.toJson(task2);
        manager.createTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
        assertEquals("Данная задача пересекается с предыдущей", response2.body());
    }

    @Test
    public void historyTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", Status.NEW, LocalDateTime.of(2015, 10, 25, 13, 20), Duration.ofMinutes(60));
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Testing task 2", Status.NEW, LocalDateTime.of(2015, 11, 25, 13, 30), Duration.ofMinutes(60));
        manager.createTask(task2);
        Epic epic = new Epic("Epic", "Testing epic", Status.NEW, LocalDateTime.of(2015, 12, 25, 13, 20), Duration.ofMinutes(60));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Testing subtask", Status.NEW, LocalDateTime.of(2016, 10, 25, 13, 30), Duration.ofMinutes(60), epic.getId());
        manager.createSubtask(subtask);
        assertEquals(0, manager.history().size());
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();
        ArrayList<Task> taskList = gson.fromJson(jsonResponse, new TasksListTypeToken().getType());
        assertEquals(4, taskList.size());
    }

    @Test
    public void prioritizedTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", Status.NEW, LocalDateTime.of(2010, 10, 25, 13, 20), Duration.ofMinutes(60));
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Testing task 2", Status.NEW, LocalDateTime.of(2014, 11, 25, 13, 30), Duration.ofMinutes(60));
        manager.createTask(task2);
        Task task3 = new Task("Task3", "Testing task 3", Status.NEW, LocalDateTime.of(2012, 12, 25, 13, 20), Duration.ofMinutes(60));
        manager.createTask(task3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();
        System.out.println(jsonResponse);
        ArrayList<Task> prioritizedList = gson.fromJson(jsonResponse, new TasksListTypeToken().getType());
        assertEquals(prioritizedList.get(0), task1);
        assertEquals(prioritizedList.get(1), task3);
        assertEquals(prioritizedList.get(2), task2);
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", Status.NEW, LocalDateTime.of(2010, 10, 25, 13, 20), Duration.ofMinutes(60));
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Testing task 2", Status.NEW, LocalDateTime.of(2014, 11, 25, 13, 30), Duration.ofMinutes(60));
        manager.createTask(task2);
        Task task3 = new Task("Task3", "Testing task 3", Status.NEW, LocalDateTime.of(2012, 12, 25, 13, 20), Duration.ofMinutes(60));
        manager.createTask(task3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();
        ArrayList<Task> tasksList = gson.fromJson(jsonResponse, new TasksListTypeToken().getType());
        assertEquals(tasksList.get(0), task1);
        assertEquals(tasksList.get(1), task2);
        assertEquals(tasksList.get(2), task3);

    }

    @Test
    public void createMoreEpicsAndSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Описание эпика1", Status.NEW);
        Epic epic2 = new Epic("Epic2", "Описание эпика2", Status.NEW);
        String epic1Json = gson.toJson(epic1);
        String epic2Json = gson.toJson(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI urlEpic = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.ofString(epic2Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        assertEquals(201, response2.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество задач");

        Subtask subtask = new Subtask("Subtask1", "Описание сабтаски1", Status.NEW, 1);
        String subtaskJson = gson.toJson(subtask);
        Subtask subtask2 = new Subtask("Subtask2", "Описание сабтаски2", Status.NEW, 1);
        String subtaskJson2 = gson.toJson(subtask2);

        URI urlSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Описание таска", Status.NEW);
        Epic epic = new Epic("Epic1", "Описание эпика1", Status.NEW);
        manager.createTask(task);
        manager.createEpic(epic);
        List<Task> tasksFromManager = manager.getAllTasks();
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, tasksFromManager.size());
        assertEquals(1, epicsFromManager.size());
        HttpClient client = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localhost:8080/tasks/1");
        URI urlEpics = URI.create("http://localhost:8080/epics/2");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTasks).DELETE().build();
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpics).DELETE().build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksAfterDelete = manager.getAllTasks();
        List<Epic> epicsAfterDelete = manager.getAllEpics();
        assertEquals(0, tasksAfterDelete.size());
        assertEquals(0, epicsAfterDelete.size());
    }

    @Test
    public void getEpicSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик1", "Описание1");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "Описание1", 1);
        Subtask subtask2 = new Subtask("Сабтаск2", "Описание2", 1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();
        System.out.println(jsonResponse);
        ArrayList<Task> subtasksList = gson.fromJson(jsonResponse, new SubtasksListTypeToken().getType());
        assertEquals(subtasksList.size(), 2);
    }
}
