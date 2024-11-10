public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Таск 1", "Поесть");
        Task task2 = new Task("Таск2", "Поспать");
        Epic epic1 = new Epic("Сделать 4 спринт", "Исправить замечания");
        Epic epic2 = new Epic("Убраться", "Убрать хату");
        Subtask subtask1 = new Subtask("Замечания", "Добавить реализацию мейна", 3);
        Subtask subtask2 = new Subtask("Сдача", "Отправить на проверку", 3);
        Subtask subtask3 = new Subtask("Пропылесосить", "Пропылеосить на кухне", 4);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        taskManager.updateTaskStatus(task1, Status.DONE);
        taskManager.updateTaskStatus(task2, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(subtask1, Status.DONE);
        taskManager.updateSubtaskStatus(subtask2, Status.DONE);
        taskManager.updateSubtaskStatus(subtask3, Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic1);
        taskManager.updateEpicStatus(epic2);
        System.out.println("__________________________________________________________________");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);
        System.out.println("__________________________________________________________________");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

    }
}
