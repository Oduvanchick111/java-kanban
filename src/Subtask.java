import java.util.ArrayList;

public class Subtask extends Task {


    private final Epic epic;
    public Subtask(String name, String details, Epic epic, int id) {
        super(name, details, id);
        this.epic = epic;
    }

    public Epic getEpic() {
        return  epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getName() + '\'' +
                ", description='" + getDetails() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epic='" + epic.getName()  + '\'' +
                '}';
    }
}