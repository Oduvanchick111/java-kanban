package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ManagerSaveException;
import com.yandex.kanban.Exceptions.ValidateException;
import com.yandex.kanban.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;


import static com.yandex.kanban.model.Task.formatter;


public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic";

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public File getFileName() {
        return file;
    }

    public void setFileName(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(HEADER);
            writer.write(System.lineSeparator());
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.write(System.lineSeparator());
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.write(System.lineSeparator());
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.write(System.lineSeparator());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                String[] partOfTask = line.split(",");
                int id = Integer.parseInt(partOfTask[0]);
                Type type = Type.valueOf(partOfTask[1]);
                String name = partOfTask[2];
                String description = partOfTask[4];
                LocalDateTime startTime;
                if (!partOfTask[5].isEmpty()) {
                    startTime = LocalDateTime.parse(partOfTask[5], formatter);
                } else {
                    startTime = null;
                }
                Duration duration;
                if (!partOfTask[6].isEmpty()) {
                    duration = Duration.ofMinutes(Integer.parseInt(partOfTask[6]));
                } else {
                    duration = null;
                }
                Status status = Status.valueOf(partOfTask[3]);
                switch (type) {
                    case TASK:
                        Task task = new Task(name, description, status, startTime, duration);
                        task.setId(id);
                        fileBackedTaskManager.createTask(task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(name, description, status, startTime, duration);
                        epic.setId(id);
                        fileBackedTaskManager.createEpic(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(name, description, status, startTime, duration, Integer.parseInt(partOfTask[7]));
                        subtask.setId(id);
                        fileBackedTaskManager.createSubtask(subtask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл");
        } catch (ValidateException e) {
            throw new RuntimeException(e);
        }
        return fileBackedTaskManager;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) throws ValidateException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) throws ValidateException {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws ValidateException {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    public String toString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",").append(task.getType()).append(",").append(task.getName()).append(",").append(task.getStatus()).append(",").append(task.getDetails()).append(",").append(task.getStartTime() != null ? task.getStartTime().format(formatter) : null).append(",").append(task.getDuration() != null ? task.getDuration().toMinutes() : null).append(",");
        if (task instanceof Subtask) {
            builder.append(((Subtask) task).getEpicId());
        }
        return builder.toString();
    }

    public static void main(String[] args) throws ValidateException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("C:\\Users\\1\\Desktop\\1.txt"));
        Epic epic = new Epic("Эпик1", "Описание1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(20));
        fileBackedTaskManager.createEpic(epic);
        Epic epic1 = new Epic("Эпик2", "Описание2");
        fileBackedTaskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Сабтаск1", "Описание1", Status.NEW, LocalDateTime.of(2025, 9, 23, 14, 0), Duration.ofMinutes(20), epic1.getId());
        fileBackedTaskManager.createSubtask(subtask);

    }
}
