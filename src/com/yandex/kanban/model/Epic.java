package com.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(String name, String details, Status status) {
        super(name, details, status);
    }

    public Epic(String name, String details, Status status, LocalDateTime startTime, Duration duration) {
        super(name, details, status, startTime, duration);
    }

    public ArrayList<Integer> getSubtasksId() {
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
        return "Epic{" +
                "title='" + getName() +
                ", description='" + getDetails() +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", StartTime=" + (getStartTime() != null ? getStartTime().format(formatter) : null) +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : null) +
                ", Id Сабтасков, входящих в данный эпик:" + subtasksId +
                '}';
    }
}