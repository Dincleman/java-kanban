import java.util.ArrayList;
import java.util.List;

// Класс для эпиков
public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();
    private Status status; // Поле для хранения статуса эпика

    public Epic(String title, String description) {
        super(title, description);
        this.status = Status.NEW; // Изначально статус нового эпика
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        calculateStatus(); // Обновляем статус при добавлении подзадачи
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    // Метод для вычисления и установки статуса эпика
    public void calculateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW; // Если подзадач нет, статус эпика NEW
            return;
        }

        boolean hasInProgress = false;
        boolean hasNew = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                status = Status.IN_PROGRESS; // Если есть подзадача в процессе, статус IN_PROGRESS
                return;
            } else if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
        }

        // Если все подзадачи NEW, статус эпика NEW
        if (hasNew) {
            status = Status.NEW;
        } else {
            // Если все подзадачи DONE, статус эпика DONE
            status = Status.DONE;
        }
    }

    // Метод для получения статуса эпика
    public Status getStatus() {
        return status;
    }
}
