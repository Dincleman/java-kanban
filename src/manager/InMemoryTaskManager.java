package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.util.*;
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
        checkTimeIntersection(task);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        save();
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
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasks().add(subtask);
        epic.calculateStatus();
        save();
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
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        save();
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
        checkTimeIntersection(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.calculateStatus();
        }
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

    @Override
    public void removeAllTasks() {
        for (Integer id : new ArrayList<>(tasks.keySet())) {
            historyManager.remove(id);
        }
        tasks.clear();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : new ArrayList<>(subtasks.keySet())) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.calculateStatus();
        }
        save();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : new ArrayList<>(epics.keySet())) {
            historyManager.remove(id);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> all = new ArrayList<>();
        all.addAll(tasks.values());
        all.addAll(subtasks.values());
        all.addAll(epics.values());
        return all;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasks = getAllTasks();
        return allTasks.stream()
                .sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + epicId + " not found");
        }
        return new ArrayList<>(epic.getSubtasks());
    }

    /**
     * Проверяет пересечение по времени новой или обновляемой задачи с уже существующими.
     * Бросает IllegalArgumentException если есть пересечение.
     */
    protected void checkTimeIntersection(Task newTask) {
        if (newTask.getStartTime() == null) {
            return; // Если нет времени — пересечений нет
        }
        for (Task task : getAllTasks()) {
            if (task.getId() == newTask.getId()) {
                continue; // Не сравниваем с самой собой
            }
            if (task.getStartTime() == null) {
                continue;
            }
            long newStart = newTask.getStartTime().toEpochSecond(java.time.ZoneOffset.UTC);
            long newEnd = newTask.getEndTime().toEpochSecond(java.time.ZoneOffset.UTC);
            long existStart = task.getStartTime().toEpochSecond(java.time.ZoneOffset.UTC);
            long existEnd = task.getEndTime().toEpochSecond(java.time.ZoneOffset.UTC);

            boolean overlap = newStart < existEnd && newEnd > existStart;
            if (overlap) {
                throw new IllegalArgumentException("Задачи пересекаются по времени с задачей id " + task.getId());
            }
        }
    }

    protected abstract void save();
}
