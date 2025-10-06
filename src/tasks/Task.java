package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // Добавлено поле для хранения времени окончания

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    // Конструктор с id, title, description, status, startTime, duration
    public Task(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : Status.NEW;
        this.startTime = startTime;
        this.duration = duration != null ? duration : Duration.ZERO;
        // Вычисляем endTime, если возможно
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        }
    }

    // Конструктор с параметрами (без id, для обратной совместимости)
    // Важно, чтобы он правильно ссылался на конструктор с 5 параметрами
    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this(0, title, description, status, startTime, duration);  // Вызов конструктора с 5 параметрами (id по умолчанию 0)
    }

    // Конструктор с параметрами (без id, для обратной совместимости)
    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this(title, description, Status.NEW, startTime, duration);  // Вызов конструктора с 5 параметрами
    }

    // Конструктор с title, description, status (без id и времени, для обратной совместимости)
    public Task(String title, String description, Status status) {
        this(title, description, status, null, Duration.ZERO);  // Вызов конструктора с 5 параметрами
    }

    // Конструктор по умолчанию
    public Task() {
        this("", "", Status.NEW, null, Duration.ZERO);  // Вызов конструктора с 5 параметрами
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration != null ? duration : Duration.ZERO;
        // Пересчитываем endTime, если startTime есть
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        // Пересчитываем endTime, если duration есть
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        }
    }

    /**
     * Возвращает время окончания задачи. Теперь возвращает хранимое значение.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Проверяет пересечение по времени с другой задачей.
     */
    public boolean intersects(Task other) {
        if (this.startTime == null || this.getEndTime() == null ||
                other.startTime == null || other.getEndTime() == null) {
            return false;
        }
        return !(this.getEndTime().isBefore(other.startTime) || this.startTime.isAfter(other.getEndTime()));
    }

    public String getName() {
        return title;
    }

    public TaskType getType() {
        return TaskType.TASK; // Для базового Task тип TASK
    }

    // Методы для изменения статуса задачи

    /**
     * Запускает задачу, меняет её статус на IN_PROGRESS.
     */
    public void startTask() {
        if (this.status == Status.NEW) {
            this.status = Status.IN_PROGRESS;
        }
    }

    /**
     * Завершает задачу, меняет её статус на DONE.
     */
    public void completeTask() {
        if (this.status == Status.IN_PROGRESS) {
            this.status = Status.DONE;
        }
    }

    /**
     * Проверяет, можно ли начать задачу.
     * Возвращает true, если задача в статусе NEW и имеет время начала.
     */
    public boolean canStart() {
        return this.status == Status.NEW && this.startTime != null;
    }

    /**
     * Проверяет, можно ли завершить задачу.
     * Возвращает true, если задача в статусе IN_PROGRESS и имеет время окончания.
     */
    public boolean canComplete() {
        return this.status == Status.IN_PROGRESS && this.endTime != null;
    }
}
