package com.yandex.kanban.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(String name, String details, Status status) {
        super(name, details, status);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getName() + '\'' +
                ", description='" + getDetails() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", Id Сабтасков, входящих в данный эпик:" + subtasksId +
                '}';
    }
}