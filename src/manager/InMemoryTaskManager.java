package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private int nextId = 1;

    @Override
    public int addNewTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeTask(int id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Tasks.Task with id " + id + " not found");
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
            epic.calculateStatus();
        }
        return subtask.getId();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskNotFoundException("Tasks.Subtask with id " + id + " not found");
        }
        subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().remove(subtask);
            epic.calculateStatus();
        }
        historyManager.remove(id);
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskNotFoundException("tasks.Epic with id " + id + " not found");
        }
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new TaskNotFoundException("Tasks.Task with id " + task.getId() + " not found");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new TaskNotFoundException("Tasks.Subtask with id " + subtask.getId() + " not found");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.calculateStatus();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new TaskNotFoundException("tasks.Epic with id " + epic.getId() + " not found");
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.calculateStatus();
        }
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(subtasks.values());
        allTasks.addAll(epics.values());
        return allTasks;
    }
}

