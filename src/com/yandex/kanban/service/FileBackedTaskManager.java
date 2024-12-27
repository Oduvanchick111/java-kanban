package com.yandex.kanban.service;

import com.yandex.kanban.Exceptions.ManagerSaveException;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


public class FileBackedTaskManager extends  InMemoryTaskManager{

    private File file;

    public FileBackedTaskManager(File file){
        this.file = file;
    }

    public File getFileName() {
        return file;
    }

    public void setFileName(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException{
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name.status,description,epic");
            writer.write("\n");
            for (Task task: getAllTasks()) {
                writer.write(toString(task));
                writer.write("\n");
            }
            for (Epic epic: getAllEpics()) {
                writer.write(toString(epic));
                writer.write("\n");
            }
            for (Subtask subtask: getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Поймано исключение");
        }
    }

    @Override
    public void removeAllTasks(){
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks(){
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics(){
        super.removeAllEpics();
        save();
    }

    @Override
    public void createTask(Task task){
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
}
