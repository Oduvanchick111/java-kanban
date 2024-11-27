package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

    @Test
    void addHistory() {
        Task task1 = new Task("Таск 1", "Поесть");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.getTask(task1.getId());
        final Task savedTask = inMemoryTaskManager.getHistoryManager().getHistory().get(task1.getId()-1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = inMemoryTaskManager.getHistoryManager().getHistory();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addHistoryMoreThan10(){
        Task task1 = new Task("Таск 1", "Поесть");
        Task task2 = new Task("Таск2", "Поспать");
        Task task3 = new Task("Таск3", "Поспать");
        Task task4 = new Task("Таск4", "Поспать");
        Task task5 = new Task("Таск5", "Поспать");
        Task task6 = new Task("Таск6", "Поспать");
        Task task7 = new Task("Таск7", "Поесть");
        Task task8 = new Task("Таск8", "Поспать");
        Task task9 = new Task("Таск9", "Поспать");
        Task task10 = new Task("Таск10", "Поспать");
        Task task11 = new Task("Таск11", "Поспать");
        Task task12 = new Task("Таск12", "Поспать");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        inMemoryTaskManager.createTask(task4);
        inMemoryTaskManager.createTask(task5);
        inMemoryTaskManager.createTask(task6);
        inMemoryTaskManager.createTask(task7);
        inMemoryTaskManager.createTask(task8);
        inMemoryTaskManager.createTask(task9);
        inMemoryTaskManager.createTask(task10);
        inMemoryTaskManager.createTask(task11);
        inMemoryTaskManager.createTask(task12);
        for (Task task: inMemoryTaskManager.getAllTasks()) {
            inMemoryTaskManager.getTask(task.getId());
        }
        final ArrayList<Task> tasks = inMemoryTaskManager.getHistoryManager().getHistory();
        assertEquals(10, tasks.size(), "Неверное количество задач.");
        assertEquals("Таск3", tasks.getFirst().getName(), "Неверное имя");
    }
}