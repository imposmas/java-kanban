public class Epic extends Task{

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", taskId=" + super.getId() +
                '}';
    }
}
