package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    void addNewTask() {
        Task task = new Task("Уборка", "Пропылесосить");
        inMemoryTaskManager.createTask(task);

        final Task savedTask = inMemoryTaskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = inMemoryTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Epic1", "Details1");
        inMemoryTaskManager.createEpic(epic);

        final Epic savedEpic = inMemoryTaskManager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final ArrayList<Epic> epics = inMemoryTaskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Epic1", "Details1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Details2", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        final Subtask savedSubtask = inMemoryTaskManager.getSubtask(subtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = inMemoryTaskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void equalsTasks() {
        Task task = new Task("Уборка", "Пропылесосить");
        inMemoryTaskManager.createTask(task);
        Task task1 = new Task("Ветеринар", "Свозить собаку в больницу");
        task1.setId(1);
        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void equalsEpic() {
        Epic epic1 = new Epic("Epic1", "Описание1");
        Epic epic2 = new Epic("Epic2", "Описание2");
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Задачи не совпадают.");
    }

    @Test
    void actualSubtasksInEpic() {
        Epic epic1 = new Epic("Epic1", "Описание1");
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Сабтаск1", "Описание1", epic1.getId());
        Subtask subtask2 = new Subtask("Сабтаск2", "Описание2", epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.getEpic(epic1.getId());
        inMemoryTaskManager.getSubtask(subtask1.getId());
        inMemoryTaskManager.getSubtask(subtask2.getId());
        assertEquals(2, inMemoryTaskManager.getEpic(epic1.getId()).getSubtasksId().size(), "Сабтаски не добавились в эпик");
        inMemoryTaskManager.removeSubtaskById(subtask1.getId());
        assertEquals(false, inMemoryTaskManager.getEpic(epic1.getId()).getSubtasksId().contains(subtask2));
    }


}