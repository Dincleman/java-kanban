package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
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
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasks().add(subtask);
        epic.calculateStatus();
        return subtask.getId();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
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
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
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
        for (Subtask subtask : new ArrayList<>(epic.getSubtasks())) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new TaskNotFoundException("Task with id " + task.getId() + " not found");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            throw new TaskNotFoundException("Subtask with id " + subtask.getId() + " not found");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.calculateStatus();
        }
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
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeAllTasks() {

    }

    @Override
    public void removeAllSubtasks() {

    }

    @Override
    public void removeAllEpics() {

    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(subtasks.values());
        allTasks.addAll(epics.values());

        return allTasks.stream()
                .sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return List.of();
    }

    protected abstract void save();
}

