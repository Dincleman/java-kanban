import java.util.*;

public class InMemoryTaskManager implements public interface TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Задачи
    @Override
    public int addNewTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task); // Добавляем задачу в историю
        }
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id); // Удаляем задачу из истории
    }

    // Подзадачи
    @Override
    public int addNewSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);

        // Добавляем подзадачу к соответствующему эпику
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }

        return subtask.getId();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask); // Добавляем подзадачу в историю
        }
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            historyManager.remove(id); // Удаляем подзадачу из истории

            // Удаляем сабтаску из коллекции сабтасок эпика
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask); // Удаляем сабтаску из коллекции эпика
                epic.updateStatus(); // Обновляем статус эпика после удаления сабтаски
            }
        }
    }

    // Эпики
    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic); // Добавляем эпик в историю
        }
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId()); // Удаляем все сабтаски, связанные с эпиком
            }
            historyManager.remove(id); // Удаляем эпик из истории
        }
    }

    // Обновление задач, подзадач и эпиков
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus(); // Обновляем статус эпика после обновления сабтаски
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // История
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory(); // Получаем историю из HistoryManager
    }

    // Удаление всех задач, подзадач и эпиков
    @Override
    public void removeAllTasks() {
        tasks.clear();
        historyManager.clear(); // Очищаем историю
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks(); // Очищаем все сабтаски у эпиков
            epic.updateStatus(); // Обновляем статус эпиков
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear(); // Очищаем все сабтаски, связанные с эпиками
        historyManager.clear(); // Очищаем историю
    }
}
