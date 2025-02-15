package com.yandex.kanban.service;

import com.yandex.kanban.exceptions.ValidateException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Status;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yandex.kanban.model.Task.formatter;

public class InMemoryTaskManager implements TaskManager {
    private int countTasks = 1;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : getAllTasks()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void createTask(Task task) {
        if (task.getId() == 0) {
            task.setId(countTasks++);
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (isFreeTime(task)) {
                prioritizedTasks.add(task);
            } else {
                throw new ValidateException("Невозможно добавить задачу с данным временным промежутком");
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(countTasks++);
        }
        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(countTasks++);
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());
        updateEpicStatus(epic);
        if (subtask.getStartTime() != null) {
            if (isFreeTime(subtask)) {
                setEpicEndTime(epic);
                prioritizedTasks.add(subtask);
            } else {
                throw new ValidateException("Невозможно добавить задачу с данным временным промежутком");
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (historyManager.getHistory().contains(tasks.get(id))) {
            historyManager.remove(id);
        }
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        if (historyManager.getHistory().contains(subtasks.get(id))) {
            historyManager.remove(id);
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksId().remove((Integer) id);
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public void removeEpicById(int id) {
        if (historyManager.getHistory().contains(epics.get(id))) {
            historyManager.remove(id);
        }
        for (Integer subtaskId : epics.get(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setName(epic.getName());
            oldEpic.setDetails(epic.getDetails());
        } else {
            System.out.println("Эпика с таким id не существует");
        }

    }

    @Override
    public ArrayList<Subtask> getSubtasks(Integer epicId) {
        List<Integer> subtasksId = epics.get(epicId).getSubtasksId();
        return (ArrayList<Subtask>) subtasksId.stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    public void updateEpicStatus(Epic epic) {
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

    public void setEpicEndTime(Epic epic) {
        Duration epicDuration = Duration.ZERO;
        Optional<LocalDateTime> epicStartTime = Optional.empty();
        Optional<LocalDateTime> epicEndTime = Optional.empty();
        if (epic.getSubtasksId() != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                epicDuration = epicDuration.plus(subtask.getDuration());
                if (subtask.getStartTime() != null) {
                    epicEndTime = epicEndTime
                            .map(endTime -> endTime.isBefore(subtask.getEndTime()) ? subtask.getEndTime() : endTime)
                            .or(() -> Optional.of(subtask.getEndTime())
                            );
                    epicStartTime = epicStartTime
                            .map(startTime -> startTime.isAfter(subtask.getStartTime()) ? subtask.getStartTime() : startTime)
                            .or(() -> Optional.of(subtask.getStartTime()));
                }
            }
        }
        epic.setDuration(epicDuration);
        epic.setStartTime(epicStartTime.orElse(null));
        epic.setEndTime(epicEndTime.orElse(null));
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean isValidateTime(Task oldTask, Task newTask) {
        LocalDateTime newTaskStartTime = newTask.getStartTime();
        LocalDateTime newTaskEndTime = newTask.getEndTime();
        LocalDateTime oldTaskStartTime = oldTask.getStartTime();
        LocalDateTime oldTaskEndTime = oldTask.getEndTime();
        if (newTaskStartTime == null) {
            return false;
        }
        if (getPrioritizedTasks().isEmpty()) {
            return true;
        }
        if (newTaskStartTime.isAfter(oldTaskStartTime) && newTaskStartTime.isBefore(oldTaskEndTime)) {
            return false;
        }
        if (newTaskEndTime.isAfter(oldTaskStartTime) && newTaskEndTime.isBefore(oldTaskEndTime)) {
            return false;
        }
        if (newTaskStartTime.isBefore(oldTaskStartTime) && newTaskEndTime.isAfter(oldTaskEndTime)) {
            return false;
        }
        return !newTaskStartTime.isEqual(oldTaskStartTime);
    }

    public boolean isFreeTime(Task task) {
        return getPrioritizedTasks().stream().allMatch(taskInList -> isValidateTime(taskInList, task));
    }

    public static void main(String[] args) throws IOException {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task4 = new Task("Таск4", "Описание4");
        Task task = new Task("Таск1", "Описание1", Status.NEW, LocalDateTime.of(2008, 1, 1, 0, 0, 0), Duration.ofMinutes(40));
        Task task1 = new Task("Таск2", "Описание1", Status.NEW, LocalDateTime.of(2005, 1, 1, 0, 0, 0), Duration.ofMinutes(40));
        Task task2 = new Task("Таск3", "Описание1", Status.NEW, LocalDateTime.of(2007, 1, 1, 0, 0, 0), Duration.ofMinutes(40));
        Epic epic = new Epic("Эпик1", "Описание", Status.NEW);
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сабтаск1", "Описание1", Status.NEW, LocalDateTime.of(2009, 1, 1, 0, 0, 0), Duration.ofMinutes(40), epic.getId());
        Subtask subtask1 = new Subtask("Сабтаск1", "Описание1", Status.NEW, LocalDateTime.of(2010, 1, 1, 0, 0, 0), Duration.ofMinutes(40), epic.getId());
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task4);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask1);
        System.out.println(inMemoryTaskManager.getPrioritizedTasks());
        System.out.println("----------------------------------------------------------");
        LocalDateTime start = epic.getStartTime();
        LocalDateTime end = epic.getEndTime();
        System.out.println(start.format(formatter));
        System.out.println(end.format(formatter));
        System.out.println(epic.getDuration().toMinutes());
        Task task3 = new Task("хуй", "пизда");
    }
}



