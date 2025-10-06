package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );
    protected int nextId = 1;

    @Override
    public int addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritized(task);
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        addToPrioritized(subtask);
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        addToHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }
        addToHistory(epic);
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epic.getSubtasks());
    }

    @Override
    public boolean updateTask(Task task) {
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        Task oldTask = tasks.get(id);
        removeFromPrioritized(oldTask);
        tasks.put(id, task);
        addToPrioritized(task);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        Subtask oldSubtask = subtasks.get(id);
        Epic epic = epics.get(oldSubtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().remove(oldSubtask);
        }
        subtasks.put(id, subtask);
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        removeFromPrioritized(oldSubtask);
        addToPrioritized(subtask);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }
        Epic oldEpic = epics.get(id);
        epic.getSubtasks().clear();
        epic.getSubtasks().addAll(oldEpic.getSubtasks());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        epics.put(id, epic);
        return true;
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        removeFromPrioritized(removed);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed == null) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.getSubtasks().remove(removed);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        removeFromPrioritized(removed);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed == null) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }
        for (Subtask subtask : removed.getSubtasks()) {
            subtasks.remove(subtask.getId());
            removeFromPrioritized(subtask);
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            removeFromPrioritized(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            removeFromPrioritized(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                removeFromPrioritized(subtask);
                historyManager.remove(subtask.getId());
            }
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void clearAll() {
        clearTasks();
        clearSubtasks();
        clearEpics();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected int generateId() {
        return nextId++;
    }

    protected void addToHistory(Task task) {
        historyManager.add(task);
    }

    protected void updateEpicStatus(Epic epic) {
        boolean allDone = true;
        boolean anyInProgress = false;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != Task.Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Task.Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }
        if (allDone) {
            epic.setStatus(Task.Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Task.Status.IN_PROGRESS);
        } else {
            epic.setStatus(Task.Status.NEW);
        }
    }

       protected void updateEpicTime(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                if (subtask.getEndTime() != null) {
                    if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                        latestEnd = subtask.getEndTime();
                    }
                }
            }
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        epic.setStartTime(earliestStart);
        epic.setDuration(totalDuration);
        epic.setEndTime(latestEnd);
    }

    protected void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    // Геттеры для доступа к коллекциям из FileBackedTaskManager
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}

