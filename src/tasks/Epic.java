package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();

    // Новый конструктор для FileBackedTaskManager (принимает id, title, description, status, startTime, duration, endTime)
    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        setEndTime(endTime); // Устанавливаем endTime явно
    }

    // Существующий конструктор (сохранён для обратной совместимости)
    public Epic(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
    }

    // Существующий конструктор (сохранён для обратной совместимости)
    public Epic(String epicTitle, String epicDescription) {
        super(epicTitle, epicDescription, Status.NEW);
        // Для эпика без времени и длительности можно оставить duration = ZERO и startTime = null
        setStartTime(null);
        setDuration(Duration.ZERO);
        setEndTime(null);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        if (getId() == subtask.getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим эпиком");
        }
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.removeIf(s -> s.getId() == subtask.getId());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks); // Возвращаем копию, чтобы защитить внутренний список
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", subtasks=" + subtasks +
                '}';
    }
}
