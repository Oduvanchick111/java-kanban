package com.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime endTime;
    private final List<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(String name, String details, Status status) {
        super(name, details, status);
    }

    public Epic(String name, String details, Status status, LocalDateTime startTime, Duration duration) {
        super(name, details, status, startTime, duration);
    }

    public Epic(String name, String details, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, details, id, status, startTime, duration);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }


    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Type: + " + getType() + "{" +
                "name='" + getName() +
                ", description='" + getDetails() +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", StartTime=" + (getStartTime() != null ? getStartTime().format(formatter) : null) +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : null) +
                ", Id Сабтасков, входящих в данный эпик:" + subtasksId +
                '}';
    }
}