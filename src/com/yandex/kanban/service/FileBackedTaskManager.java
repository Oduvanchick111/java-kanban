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
            writer.write("id,type,name,status,description,epic");
            writer.write("\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.write("\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.write("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Поймано исключение");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.ready()) {
                String task = reader.readLine();
                String[] partOfTask = task.split(",");
                int id = Integer.parseInt(partOfTask[0]);
                Type type = Type.valueOf(partOfTask[1]);
                String name = partOfTask[2];
                String description = partOfTask[4];
                Status status = Status.valueOf(partOfTask[3]);
                switch (type) {
                    case TASK:
                        Task task1 = new Task(name, description, status);
                        task1.setId(id);
                        fileBackedTaskManager.createTask(task1);
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
            throw new ManagerSaveException("Поймано исключение");
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

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
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
        Task task = new Task("Таск1", "Описание1");
        fileBackedTaskManager.createTask(task);
        Task task1 = new Task("Таск2", "Описание11");
        fileBackedTaskManager.createTask(task1);
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
