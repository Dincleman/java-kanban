import java.util.ArrayList;
import java.util.List;

// Класс для эпиков
public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public Status calculateStatus() {
        if (subtasks.isEmpty()) {
            return Status.NEW; // Если подзадач нет, статус эпика NEW
        }

        boolean hasInProgress = false;
        boolean hasNew = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                return Status.IN_PROGRESS; // Можно сразу возвращать статус IN_PROGRESS
            } else if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
        }

        // Если все подзадачи NEW, статус эпика NEW
        if (hasNew) {
            return Status.NEW;
        }

        // Если все подзадачи DONE, статус эпика DONE
        return Status.DONE;
    }
}
