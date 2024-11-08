import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public int countTasks;
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epics;


    TaskManager() {
        countTasks = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        return allTasks;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        allSubtasks.addAll(subtasks.values());
        return allSubtasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        allEpics.addAll(epics.values());
        return allEpics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtaks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public Task createTask(String name, String details) {
        Task task = new Task(name, details, ++countTasks);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String name, String details) {
        Epic epic = new Epic(name, details, ++countTasks);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(String name, String details, Epic epic) {
        Subtask subtask = new Subtask(name, details, epic, ++countTasks);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        return subtask;
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public ArrayList<Subtask> getSubtasks(Epic epic) {
        return epic.subtasks;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;
            for (Subtask subtask: epic.getSubtasks()) {
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
            }
            if (allDone) {
                epic.setStatus(Status.DONE);
            } else if (allNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }

    }
}
