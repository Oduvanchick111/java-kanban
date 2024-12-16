package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

    @Test
    void addHistory() {
        Task task1 = new Task("Таск 1", "Поесть");
        taskManager.createTask(task1);
        taskManager.getTask(task1.getId());
        final Task savedTask = taskManager.history().get(task1.getId() - 1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.history();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void removeTasks() {
        Task task = new Task("Таск1", "Поспать");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        final Task savedTask = taskManager.history().get(task.getId() - 1);
        assertEquals(savedTask, task, "Задачи не совпадают");
        taskManager.removeTaskById(task.getId());
        System.out.println(taskManager.history().size());
        assertEquals(0, taskManager.history().size(), "Лист не пустой");
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
    }

}