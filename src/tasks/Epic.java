package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtask(Subtask subtask) {
        if (getId() == subtask.getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим эпиком");
        }
        subtasks.add(subtask);
        calculateStatus();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void calculateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean hasInProgress = false;
        boolean hasNew = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                setStatus(Status.IN_PROGRESS);
                return;
            } else if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
        }

        setStatus(hasNew ? Status.NEW : Status.DONE);
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
