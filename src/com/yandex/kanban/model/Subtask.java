package com.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public Subtask(String name, String details, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, details, status, startTime, duration);
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
                "name='" + getName() +
                ", description='" + getDetails() +
                ", id=" + getId() + '\'' +
                ", status=" + getStatus() + '\'' +
                "StartTime=" + (getStartTime() != null ? getStartTime().format(formatter) : null) +
                "duration=" + (getDuration() != null ? getDuration().toMinutes() : null) +
                ", epicId='" + epicId +
                '}';
    }
}