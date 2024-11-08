import java.util.ArrayList;

public class Epic extends  Task {
    public ArrayList<Subtask> subtasks;



    public Epic(String name, String details, int id) {
        super(name, details, id);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setStatus(Status.NEW);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getName() + '\'' +
                ", description='" + getDetails() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", Сабтаски входящие в данный эпик:" + subtasks +
                '}';
    }
}