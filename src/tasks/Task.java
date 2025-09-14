package tasks;

import java.time.Duration;
import java.time.LocalDateTime;


public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    // Конструктор с двумя параметрами (статус по умолчанию NEW)
    public Task(String title, String description, LocalDateTime startTime,  Duration duration) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Новый конструктор с тремя параметрами (статус передается явно)
    public Task(String title, String description, Status status, LocalDateTime startTime,  Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration); // уточнить про сеттер- он же не нужен, т.к. мы EndTime вычисляем в методе?
    }
    
    //сеттер под вопросом
    public void setEndTime() {
        this.endTime = endTime;
    }

    // Переопределение equals и hashCode для корректного сравнения задач (нужно для списков и проверок пересечений)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id; // Сравнение по ID (предполагаем, что ID уникален)
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
