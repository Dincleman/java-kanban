package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.util.*;

public abstract class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager;
    protected int nextId = 1;

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager() {
            @Override
            public void clear() {

            }
        };
    }

    @Override
    public int addNewTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        checkTimeIntersection(task);
        if (task.getId() == 0) {
            task.setId(nextId++);
        } else if (task.getId() >= nextId) {
            nextId = task.getId() + 1;
        }
        tasks.put(task.getId(), task);
        save();
        return task.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeTask(int id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        tasks.remove(id);
        historyManager.remove(id);
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + subtask.getEpicId() + " not found");
        }
        checkTimeIntersection(subtask);
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        } else if (subtask.getId() >= nextId) {
            nextId = subtask.getId() + 1;
        }
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasks().add(subtask);
        epic.calculateStatus();
        save();
        return subtask.getId();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().removeIf(st -> st.getId() == id);
            epic.calculateStatus();
        }
        subtasks.remove(id);
        historyManager.remove(id);
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        } else if (epic.getId() >= nextId) {
            nextId = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
        save();
        return epic.getId();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }
        // Удаляем все подзадачи эпика
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epic.getSubtasks().clear();
        epics.remove(id);
        historyManager.remove(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new TaskNotFoundException("Task with id " + task.getId() + " not found");
        }
        checkTimeIntersection(task);
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            throw new TaskNotFoundException("Subtask with id " + subtask.getId() + " not found");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + subtask.getEpicId() + " not found");
        }
        checkTimeIntersection(subtask);
        subtasks.put(subtask.getId(), subtask);

        // Обновляем подзадачу в списке эпика
        List<Subtask> epicSubtasks = epic.getSubtasks();
        for (int i = 0; i < epicSubtasks.size(); i++) {
            if (epicSubtasks.get(i).getId() == subtask.getId()) {
                epicSubtasks.set(i, subtask);
                break;
            }
        }
        epic.calculateStatus();
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        if (!epics.containsKey(epic.getId())) {
            throw new TaskNotFoundException("Epic with id " + epic.getId() + " not found");
        }
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Метод для проверки пересечений по времени (примерная реализация)
    protected void checkTimeIntersection(Task newTask) {
        // Проверяем пересечения с задачами
        for (Task task : tasks.values()) {
            if (newTask.intersects(task)) {
                throw new IllegalArgumentException("Task time intersects with existing task id " + task.getId());
            }
        }
        // Проверяем пересечения с подзадачами
        for (Subtask subtask : subtasks.values()) {
            if (newTask.intersects(subtask)) {
                throw new IllegalArgumentException("Task time intersects with existing subtask id " + subtask.getId());
            }
        }
        // Проверяем пересечения с эпиками, если у эпиков есть время
        for (Epic epic : epics.values()) {
            if (newTask.intersects(epic)) {
                throw new IllegalArgumentException("Task time intersects with existing epic id " + epic.getId());
            }
        }
    }

    // Абстрактный метод сохранения, реализуется в наследниках
    protected abstract void save();

// Дополнительные методы класса

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritized = new ArrayList<>();
        prioritized.addAll(tasks.values());
        prioritized.addAll(subtasks.values());
        prioritized.sort(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return prioritized;
    }

    @Override
    public void clearAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clear();
        nextId = 1;
        save();
    }
}



