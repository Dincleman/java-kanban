package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
    }

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

    @Override
    public String getName() {
        return getTitle();
    }

    public void addSubtask(Subtask subtask) {
        if (getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим эпиком");
        }
        subtasks.add(subtask);
        calculateStatus();
        updateTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.removeIf(s -> s.getId() == subtask.getId());
        calculateStatus();
        updateTime();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks); // Возвращаем копию, чтобы защитить внутренний список
    }

    /**
     * Обновляет статус эпика в зависимости от статусов подзадач.
     */
    public void calculateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean hasNew = false;
        boolean hasInProgress = false;

        for (Subtask subtask : subtasks) {
            Status st = subtask.getStatus();
            if (st == Status.IN_PROGRESS) {
                hasInProgress = true;
            } else if (st == Status.NEW) {
                hasNew = true;
            }
        }

        if (hasInProgress) {
            setStatus(Status.IN_PROGRESS);
        } else if (hasNew && !hasInProgress) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.DONE);
        }
    }

    /**
     * Обновляет время начала, окончания и длительность эпика, исходя из подзадач.
     */
    public void updateTime() {
        if (subtasks.isEmpty()) {
            setStartTime(null);
            setDuration(Duration.ZERO);
            setEndTime(null);
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            LocalDateTime st = subtask.getStartTime();
            LocalDateTime en = subtask.getEndTime();

            if (st != null) {
                if (start == null || st.isBefore(start)) {
                    start = st;
                }
            }
            if (en != null) {
                if (end == null || en.isAfter(end)) {
                    end = en;
                }
            }
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        setStartTime(start);
        setEndTime(end);
        setDuration(totalDuration);
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
