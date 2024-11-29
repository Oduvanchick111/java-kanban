package com.yandex.kanban;
import com.yandex.kanban.model.Epic;
import com.yandex.kanban.model.Subtask;
import com.yandex.kanban.model.Task;
import com.yandex.kanban.service.InMemoryTaskManager;


public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Таск 1", "Поесть");
        Task task2 = new Task("Таск2", "Поспать");
        Task task3 = new Task("Таск3", "Поспать");
        Task task4 = new Task("Таск4", "Поспать");
        Task task5 = new Task("Таск5", "Поспать");
        Task task6 = new Task("Таск6", "Поспать");
        Task task7 = new Task("Таск7", "Поесть");
        Task task8 = new Task("Таск8", "Поспать");
        Task task9 = new Task("Таск9", "Поспать");
        Task task10 = new Task("Таск10", "Поспать");
        Task task11 = new Task("Таск11", "Поспать");
        Task task12 = new Task("Таск12", "Поспать");
        Epic epic1 = new Epic("Сделать 4 спринт", "Исправить замечания");
        Epic epic2 = new Epic("Убраться", "Убрать хату");
//        inMemoryTaskManager.createEpic(epic1);
//        inMemoryTaskManager.createEpic(epic2);

        System.out.println(epic1.getId());
        Subtask subtask1 = new Subtask("Замечания", "Добавить реализацию мейна", epic1.getId());
        Subtask subtask2 = new Subtask("Сдача", "Отправить на проверку", epic2.getId());
        Subtask subtask3 = new Subtask("Пропылесосить", "Пропылеосить на кухне", epic2.getId());
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        inMemoryTaskManager.createTask(task4);
        inMemoryTaskManager.createTask(task5);
        inMemoryTaskManager.createTask(task6);
        inMemoryTaskManager.createTask(task7);
        inMemoryTaskManager.createTask(task8);
        inMemoryTaskManager.createTask(task9);
        inMemoryTaskManager.createTask(task10);
        inMemoryTaskManager.createTask(task11);
        inMemoryTaskManager.createTask(task12);
//        inMemoryTaskManager.createSubtask(subtask1);
//        inMemoryTaskManager.createSubtask(subtask2);
//        inMemoryTaskManager.createSubtask(subtask3);
        for (Task task: inMemoryTaskManager.getAllTasks()) {
            inMemoryTaskManager.getTask(task.getId());
        }
        for (Subtask subtask: inMemoryTaskManager.getAllSubtasks()) {
            inMemoryTaskManager.getSubtask(subtask.getId());
        }
        for (Epic epic: inMemoryTaskManager.getAllEpics()) {
            inMemoryTaskManager.getEpic(epic.getId());
        }
        System.out.println(inMemoryTaskManager.history().get(0).getName());



    }
}
