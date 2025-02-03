package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    protected InMemoryTaskManager taskManager;

    @BeforeEach
    void fillHistory() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        task = new Task("Таск1", "ТОписание1");
        epic = new Epic("Эпик1", "Описание2");
        subtask1 = new Subtask("Сабтаск1", "Описание3", 2);
        subtask2 = new Subtask("Сабтаск2", "Описание4", 2);
    }


    @Test
    void addHistory() {
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        final Task savedTask = taskManager.history().get(task.getId() - 1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.history();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void removeTasks() {
        Task task1 = new Task("Таск1", "details1");
        Task task2 = new Task("Таск2", "details2");
        Task task3 = new Task("Таск3", "details3");
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.getTask(task.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        final Task savedTask = taskManager.history().get(task.getId() - 1);
        assertEquals(savedTask, task, "Задачи не совпадают");
        taskManager.removeTaskById(task.getId());
        assertEquals(3, taskManager.history().size(), "Задача не удалена");
        taskManager.removeTaskById(task2.getId());
        assertEquals(2, taskManager.history().size(), "Задача не удалена");
        taskManager.removeTaskById(task3.getId());
        assertEquals(1, taskManager.history().size(), "Задача не удалена");
    }

    @Test
    void checkUniqueness() {
        Task task1 = new Task("Таск1", "details1");
        Task task2 = new Task("Таск2", "details2");
        Task task3 = new Task("Таск3", "details3");
        Task task4 = new Task("Таск4", "details4");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task4.getId());
        taskManager.getTask(task1.getId());
        assertEquals(task1, taskManager.history().get(3));
        assertEquals(task2, taskManager.history().get(0));
        assertEquals(4, taskManager.history().size());
    }

    @Test
    void checkEmptyHistory() {
        assertEquals(true, taskManager.history().isEmpty());
    }
}