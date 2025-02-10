package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Status;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void prepareData() throws IOException {
        taskManager = getTaskManager();
        task = new Task("Таск1", "Описание1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.createTask(task);
        epic = new Epic("Эпик1", "Описание2", Status.NEW, LocalDateTime.of(2025, 10, 23, 14, 0), Duration.ofMinutes(60));
        taskManager.createEpic(epic);
        subtask1 = new Subtask("Сабтаск1", "Описание3", Status.NEW, LocalDateTime.of(2025, 9, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        subtask2 = new Subtask("Сабтаск2", "Описание4", Status.NEW, LocalDateTime.of(2025, 11, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

    protected abstract T getTaskManager();

    @Test
    void addNewTask() throws IOException {
        final ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void checkExistenceOfEpic() {
        assertEquals(epic, taskManager.getEpic(subtask1.getEpicId()), "Не тот эпик");
    }

    @Test
    void addNewEpic() {
        final Epic savedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        final Subtask savedSubtask = taskManager.getSubtask(subtask1.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void equalsTasks() throws IOException {
        Task task1 = new Task("Ветеринар", "Свозить собаку в больницу");
        task1.setId(1);
        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void equalsEpic() {
        Epic epic2 = new Epic("Эпик2", "Описание2", Status.NEW, LocalDateTime.of(2027, 10, 23, 14, 0), Duration.ofMinutes(60));
        taskManager.createEpic(epic2);
        epic2.setId(epic.getId());
        assertEquals(epic, epic2, "Задачи не совпадают.");
    }

    @Test
    void actualSubtasksInEpic() {
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        assertEquals(2, taskManager.getEpic(epic.getId()).getSubtasksId().size(), "Сабтаски не добавились в эпик");
        taskManager.removeSubtaskById(subtask1.getId());
        assertFalse(taskManager.getEpic(epic.getId()).getSubtasksId().contains(subtask2));
    }

    @Test
    void checkTimeException() throws IOException {
        Task task1 = new Task("Таск1", "Описание1", Status.NEW, LocalDateTime.of(2025, 10, 23, 14, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Таск1", "Описание1", Status.NEW, LocalDateTime.of(2025, 10, 23, 14, 10), Duration.ofMinutes(60));
        taskManager.createTask(task1);
        assertThrows(ValidateException.class, () -> taskManager.createTask(task2));
    }
}

