package com.yandex.kanban.model;

import java.util.Objects;

public class Task {

    private String name;
    private String details;
    private Status status;
    private int id;

    public Task(String name, String details) {
        this.name = name;
        this.details = details;
        this.status = Status.NEW;
    }

    public Task(String name, String details, Status status) {
        this.name = name;
        this.details = details;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return Type.TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
