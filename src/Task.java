import java.util.*;
// Enum для статусов задач
public enum Status {
    NEW,
    IN_PROGRESS,
    DONE
}
// Базовый класс для задач
public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    // Геттеры и сеттеры
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
// Класс для подзадач
public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
// Класс для эпиков
import java.util.ArrayList;
import java.util.List;

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
                hasInProgress = true;
            } else if (subtask.getStatus() == Status.NEW) {
                hasNew = true;
            }
        }

        // Если есть хотя бы одна подзадача в процессе, статус эпика IN_PROGRESS
        if (hasInProgress) {
            return Status.IN_PROGRESS;
        }
        // Если все подзадачи NEW, статус эпика NEW
        if (hasNew) {
            return Status.NEW;
        }
        // Если все подзадачи DONE, статус эпика DONE
        return Status.DONE;
    }
}
// Менеджер задач
public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 1;

    // Создание задачи
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    // Создание эпика
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    // Создание подзадачи
    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        epic.calculateStatus(); // Обновляем статус эпика при добавлении подзадачи
    }

    // Получение списка всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение списка всех эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение списка всех подзадач
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение подзадач по эпикам
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    // Удаление задачи по идентификатору
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // Удаление эпика по идентификатору
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    // Удаление подзадачи по идентификатору
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = subtask.getEpic();
            epic.getSubtasks().remove(subtask);
            epic.calculateStatus(); // Обновляем статус эпика при удалении подзадачи
        }
    }

    // Получение задачи по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Получение эпика по идентификатору
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Обновление задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Обновление эпика
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.calculateStatus(); // Обновляем статус эпика при изменении подзадачи
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }
}



