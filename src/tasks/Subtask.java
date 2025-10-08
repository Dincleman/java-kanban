package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    // Новый конструктор для FileBackedTaskManager (принимает id, title, description, status, epicId, startTime, duration, endTime)
    public Subtask(int id, String title, String description, Status status, int epicId, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        setEpicId(epicId);
        setEndTime(endTime); // Устанавливаем endTime явно
    }

    // Существующие конструкторы (сохранены для обратной совместимости)
    public Subtask(String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
        setEpicId(epicId);
    }

    public Subtask(String title, String description, int epicId) {
        super(title, description, Status.NEW, null, Duration.ZERO);
        setEpicId(epicId);
    }

    public Subtask(String title, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        setEpicId(epicId);
    }

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status, null, Duration.ZERO);
        setEpicId(epicId);
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
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
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
