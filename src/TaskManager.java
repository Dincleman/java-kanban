import java.util.*;

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
        Epic epic = epics.get(subtask.getEpicId()); // Получаем эпик по ID
        if (epic != null) {
            epic.addSubtask(subtask);
            epic.calculateStatus(); // Обновляем статус эпика при добавлении подзадачи
        }
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

    // Получение подзадач по идентификатору эпика
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
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
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                epic.calculateStatus(); // Обновляем статус эпика при удалении подзадачи
            }
        }
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаление всех эпиков
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear(); // Удаляем все подзадачи, связанные с эпиками
    }

    // Удаление всех подзадач
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear(); // Обновляем списки подзадач в эпиках
            epic.calculateStatus(); // Обновляем статус эпика
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
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.calculateStatus(); // Обновляем статус эпика при изменении подзадачи
        }
    }
}
