public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getName() + '\'' +
                ", description='" + getDetails() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId='" + epicId  + '\'' +
                '}';
    }
}