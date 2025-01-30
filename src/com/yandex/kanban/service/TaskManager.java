package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public interface TaskManager {
    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks() throws IOException;

    void removeAllSubtasks() throws IOException;

    void removeAllEpics() throws IOException;

    void createTask(Task task) throws IOException, ValidateException;

    void createEpic(Epic epic) throws ValidateException;

    void createSubtask(Subtask subtask) throws ValidateException;

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    ArrayList<Subtask> getSubtasks(Integer epicId);

    List<Task> history();

    Set<Task> getPrioritizedTasks();

}
