package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    protected void updateEpicTime(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        Optional<LocalDateTime> earliestStart = epic.getSubtasks().stream()
                .map(Subtask::getStartTime)
                .min(Comparator.naturalOrder());

        Duration totalDuration = epic.getSubtasks().stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(earliestStart.get());
        epic.setDuration(totalDuration);
        epic.setEndTime(earliestStart.get().plus(totalDuration));
    }

    /**
     * Проверка пересечения по времени двух задач.
     * @param task1 первая задача
     * @param task2 вторая задача
     * @return true, если задачи пересекаются по времени, иначе false
     */
     boolean intersects(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        return end1.isAfter(start2) && end2.isAfter(start1);
    }

    protected void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            List<Task> tmpTaskList = prioritizedTasks.stream()
                    .filter(fTask -> intersects(task, fTask))
                    .collect(Collectors.toList());
            if (!tmpTaskList.isEmpty())
                System.out.println("Внимание, добавляемая задача пересекается с другими");
            else
                prioritizedTasks.add(task);
        }
    }

    protected void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

}

