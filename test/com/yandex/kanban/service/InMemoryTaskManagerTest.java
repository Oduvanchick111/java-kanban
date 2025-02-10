package com.yandex.kanban.service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}



