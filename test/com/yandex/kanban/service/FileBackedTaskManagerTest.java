package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Status;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {


    @Override
    protected FileBackedTaskManager getTaskManager() {
        return new FileBackedTaskManager(new File("C:\\Users\\1\\Desktop\\1.txt"));
    }

    FileBackedTaskManager fileBackedTaskManager;
    File file;

    {
        file = new File("C:\\Users\\1\\Desktop\\1.txt");
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void load() throws ValidateException {
        Task task = new Task("Таск1", "Описание1", Status.NEW, LocalDateTime.of(2020, 10, 23, 14, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Таск2", "Описание2", Status.NEW, LocalDateTime.of(2021, 10, 23, 14, 0), Duration.ofMinutes(60));
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task2);
        epic = new Epic("Эпик1", "Описание2");
        fileBackedTaskManager.createEpic(epic);
        subtask1 = new Subtask("Сабтаск1", "Описание3", Status.NEW, LocalDateTime.of(2025, 9, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        subtask2 = new Subtask("Сабтаск2", "Описание4", Status.NEW, LocalDateTime.of(2025, 11, 23, 14, 0), Duration.ofMinutes(60), epic.getId());
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(fileBackedTaskManager1.getTask(task.getId()), task);
        assertEquals(fileBackedTaskManager1.getTask(task2.getId()), task2);
        assertEquals(fileBackedTaskManager1.getEpic(epic.getId()), epic);
        assertEquals(fileBackedTaskManager1.getSubtask(subtask1.getId()), subtask1);
        assertEquals(fileBackedTaskManager1.getSubtask(subtask2.getId()), subtask2);
    }
}