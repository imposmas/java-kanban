import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    private List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", taskId=" + super.getId() +
                ", subtasks=" + subTasks +
                '}';
    }
}
