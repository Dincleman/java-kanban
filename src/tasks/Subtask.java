package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
        setEpicId(epicId);
    }

    public Subtask(String title, String description, int epicId) {
        super();
    }

    public Subtask(String testSubtask, String desc, Status status, int epicId, LocalDateTime startTime, Duration duration) {
    }

    public Subtask(String name, String description, Status status, int epicId) {
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим эпиком");
        }
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Tasks.Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
