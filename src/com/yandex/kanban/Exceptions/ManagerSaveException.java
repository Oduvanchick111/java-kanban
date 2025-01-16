package com.yandex.kanban.Exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
