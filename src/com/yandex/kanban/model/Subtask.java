package com.yandex.kanban.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public Subtask(String name, String details, Status status, int epicId) {
        super(name, details, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getName() + '\'' +
                ", description='" + getDetails() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId='" + epicId + '\'' +
                '}';
    }
}