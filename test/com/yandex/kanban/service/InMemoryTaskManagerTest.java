package com.yandex.kanban.service;

import com.yandex.kanban.model.Epic;

import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}



