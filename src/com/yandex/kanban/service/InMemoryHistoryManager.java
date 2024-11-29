package com.yandex.kanban.service;

import com.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> history;
    static final int HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void remove(int id) {
        List<Task> tasksToRemove = new ArrayList<>();
        for (Task task : history) {
            if (task.getId() == id) {
                tasksToRemove.add(task);
            }
        }
        history.removeAll(tasksToRemove); // Удаляем все собранные задачи сразу
    }
}
