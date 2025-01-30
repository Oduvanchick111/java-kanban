package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    File file;

    {
        try {
            file = File.createTempFile("Tasks", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    void addNewTask() throws IOException, ValidateException {
        Task task = new Task("Уборка", "Пропылесосить");
        fileBackedTaskManager.createTask(task);

        final Task savedTask = fileBackedTaskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = fileBackedTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addMultipleTasks() throws FileNotFoundException, ValidateException {
        List<String> listOfTasks = new ArrayList<>();
        Task task = new Task("Таск1", "Описание1");
        fileBackedTaskManager.createTask(task);
        Task task1 = new Task("Таск2", "Описание11");
        fileBackedTaskManager.createTask(task1);
        Epic epic = new Epic("Эпик1", "Описание2");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сабатск1", "Описание 3", epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Task task2 = new Task("Таск3", "Описание111");
        fileBackedTaskManager.createTask(task2);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String read = bufferedReader.readLine();
                listOfTasks.add(read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("id,type,name,status,description,epic", listOfTasks.get(0));
        assertEquals(fileBackedTaskManager.toString(task), listOfTasks.get(1));
        assertEquals(fileBackedTaskManager.toString(task1), listOfTasks.get(2));
        assertEquals(fileBackedTaskManager.toString(epic), listOfTasks.get(4));
        assertEquals(fileBackedTaskManager.toString(subtask), listOfTasks.get(5));
        assertEquals(fileBackedTaskManager.toString(task2), listOfTasks.get(3));
    }

    @Test
    public void load() throws ValidateException {
        Task task = new Task("Таск1", "Описание1");
        fileBackedTaskManager.createTask(task);
        Task task1 = new Task("Таск2", "Описание11");
        fileBackedTaskManager.createTask(task1);
        Epic epic = new Epic("Эпик1", "Описание2");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сабатск1", "Описание 3", epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Task task2 = new Task("Таск3", "Описание111");
        fileBackedTaskManager.createTask(task2);
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(fileBackedTaskManager1.getTask(task.getId()), task);
        assertEquals(fileBackedTaskManager1.getTask(task1.getId()), task1);
        assertEquals(fileBackedTaskManager1.getTask(task2.getId()), task2);
        assertEquals(fileBackedTaskManager1.getEpic(epic.getId()), epic);
        assertEquals(fileBackedTaskManager1.getSubtask(subtask.getId()), subtask);

    }
}