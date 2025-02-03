package com.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task {

    private String name;
    private String details;
    private Status status;
    private int id;
    private Duration duration;
    private LocalDateTime startTime;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");


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

    public Task(String name, String details, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                ", details='" + details +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + (startTime != null ? startTime.format(formatter) : null) +
                ", duration=" + (duration != null ? duration.toMinutes() : null) +
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
