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
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");


    public Task(String name, String details) {
        this.name = name;
        this.details = details;
        this.status = Status.NEW;
        startTime = null;
        duration = null;
    }

    public Task(String name, String details, Status status) {
        this.name = name;
        this.details = details;
        this.status = status;
        startTime = null;
        duration = null;
    }

    public Task(String name, String details, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String details, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.details = details;
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getDetails() {
        return details;
    }

    public Status getStatus() {
        return status;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Task{name='%s', details='%s', status=%s, id=%d, startTime=%s, duration=%s}",
                name,
                details,
                status,
                id,
                (startTime != null ? startTime.format(formatter) : "null"),
                (duration != null ? duration.toMinutes() : "null"));
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
