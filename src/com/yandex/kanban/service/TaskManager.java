package com.yandex.kanban.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Status;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.model.Subtask;

public class TaskManager {
    private int countTasks = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;


    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }

    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void createTask(Task task) {
        task.setId(countTasks++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(countTasks++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(countTasks++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());
        updateEpicStatus(epic);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksId().remove((Integer)id);
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public void removeEpicById(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDetails(epic.getDetails());
    }

    public ArrayList<Subtask> getSubtasks(Integer epicId) {
        ArrayList<Subtask> subtasksValues = new ArrayList<>();
        ArrayList<Integer> subtasksId = epics.get(epicId).getSubtasksId();
        for (Integer subtaskId : subtasksId) {
            subtasksValues.add(subtasks.get(subtaskId));
        }
        return subtasksValues;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;
            for (Integer subtaskId : epic.getSubtasksId()) {
                Status subtaskStatus = subtasks.get(subtaskId).getStatus();
                if (subtaskStatus != Status.DONE) {
                    allDone = false;
                }
                if (subtaskStatus != Status.NEW) {
                    allNew = false;
                }
            }
            if (allDone) {
                epic.setStatus(Status.DONE);
            } else if (allNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }

    }
}
