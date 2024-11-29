package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();

    void remove(int id);
}
