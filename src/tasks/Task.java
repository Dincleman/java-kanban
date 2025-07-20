package tasks;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;

    // Конструктор с двумя параметрами (статус по умолчанию NEW)
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    // Новый конструктор с тремя параметрами (статус передается явно)
    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
