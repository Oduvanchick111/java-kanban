package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ManagerSaveException;
import com.yandex.kanban.model.*;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

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
            final String header = "id,type,name,status,description,epic";
            writer.write(header);
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
                Status status = Status.valueOf(partOfTask[3]);
                switch (type) {
                    case TASK:
                        Task task = new Task(name, description, status);
                        task.setId(id);
                        fileBackedTaskManager.createTask(task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(name, description, status);
                        epic.setId(id);
                        fileBackedTaskManager.createEpic(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(name, description, status, Integer.parseInt(partOfTask[5]));
                        subtask.setId(id);
                        fileBackedTaskManager.createSubtask(subtask);
                        break;
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("");
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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
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
        builder.append(task.getId()).append(",").append(task.getType()).append(",").append(task.getName()).append(",").append(task.getStatus()).append(",").append(task.getDetails()).append(",");
        if (task instanceof Subtask) {
            builder.append(((Subtask) task).getEpicId());
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("C:\\Users\\liza1\\OneDrive\\Рабочий стол\\2.txt"));
        Task firstTask = new Task("Таск1", "Описание1");
        fileBackedTaskManager.createTask(firstTask);
        Task secondTask = new Task("Таск2", "Описание11");
        fileBackedTaskManager.createTask(secondTask);
        Epic epic = new Epic("Эпик1", "Описание2");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сабатск1", "Описание 3", epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Task task2 = new Task("Таск3", "Описание111");
        fileBackedTaskManager.createTask(task2);
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(new File("C:\\Users\\liza1\\OneDrive\\Рабочий стол\\2.txt"));
        System.out.println(fileBackedTaskManager1.getAllTasks());
        System.out.println(fileBackedTaskManager1.getAllEpics());
    }
}
