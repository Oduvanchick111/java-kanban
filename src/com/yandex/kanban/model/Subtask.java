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

    public Subtask(String name, String details, int id, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, details, id, status, startTime, duration);
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
        return String.format("Type: '%s' {name= '%s', description= '%s', status= '%s', id= '%d', startTime= '%s', duration= '%s', epicId= '%d'}",
                getType(),
                getName(),
                getDetails(),
                getStatus(),
                getId(),
                (getStartTime() != null ? getStartTime().format(formatter) : "null"),
                (getDuration() != null ? getDuration().toMinutes() : "null"),
                getEpicId());
    }
}
