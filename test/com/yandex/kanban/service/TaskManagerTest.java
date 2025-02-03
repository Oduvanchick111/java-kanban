package com.yandex.kanban.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;
    TaskManager inMemoryTaskManager = Managers.getDefault();

    void prepareData() throws IOException {
        task = new Task("Покупки", "Описание1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.createTask(task);
        epic = new Epic("Большая задача1", "Описание2", Status.NEW, LocalDateTime.of(2025, 10, 23, 14, 0), Duration.ofMinutes(60));
        taskManager.createEpic(epic);
        subtask1 = new Subtask("Сабтаск1", "Описание3", Status.NEW, LocalDateTime.of(2025, 9, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        subtask2 = new Subtask("Сабтаск2", "Описание4", Status.NEW, LocalDateTime.of(2025, 11, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }
    void addNewTask() throws IOException {
        final ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void checkExistenceOfEpic() {
        Epic epic = new Epic("Эпик1", "Описание1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сабтаск1", "Описание2", epic.getId());
        assertEquals(epic, inMemoryTaskManager.getEpic(subtask.getEpicId()), "Не тот эпик");
    }
}
