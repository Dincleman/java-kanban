package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private int nextId = 1;

    @Override
    public int addNewTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritized(task);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
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
    public int addNewEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            addToHistory(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            addToHistory(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            addToHistory(epic);
        }
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
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            removeFromPrioritized(tasks.get(id));
            addToPrioritized(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
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
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic oldEpic = epics.get(id);
            epic.getSubtasks().clear();
            epic.getSubtasks().addAll(oldEpic.getSubtasks());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            epics.put(id, epic);
        }
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            removeFromPrioritized(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(removed);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            removeFromPrioritized(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            for (Subtask subtask : removed.getSubtasks()) {
                subtasks.remove(subtask.getId());
                removeFromPrioritized(subtask);
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            removeFromPrioritized(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritized(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritized(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int generateId() {
        return nextId++;
    }

    private void addToHistory(Task task) {
        historyManager.add(task);
    }

    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updateEpicStatus(Epic epic) {
        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != Task.Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Task.Status.NEW) {
                allNew = false;
            }
        }
        if (allDone && !epic.getSubtasks().isEmpty()) {
            epic.setStatus(Task.Status.DONE);
        } else if (allNew || epic.getSubtasks().isEmpty()) {
            epic.setStatus(Task.Status.NEW);
        } else {
            epic.setStatus(Task.Status.IN_PROGRESS);
        }
    }


    private void updateEpicTime(Epic epic) {
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
                LocalDateTime subEnd = subtask.getEndTime();
                if (latestEnd == null || (subEnd != null && subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd;
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

    @Override
    public void clearAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clear();
        prioritizedTasks.clear();
    }
}


