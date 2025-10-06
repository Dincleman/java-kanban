//package tasks;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import manager.TaskManager;
import tasks.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    // Инициализация для каждого теста
    @BeforeEach
    public void setUp() {
        manager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    public void testAddTask() {
        Task task = new Task("Task 1", "Description 1", Task.Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = manager.addTask(task);
        Task loadedTask = manager.getTask(taskId);

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getStatus(), loadedTask.getStatus());
    }

    @Test
    public void testAddSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1", LocalDateTime.now(), Duration.ofMinutes(120));
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Task.Status.NEW, epicId, LocalDateTime.now(), Duration.ofMinutes(60));
        int subtaskId = manager.addSubtask(subtask);

        Subtask loadedSubtask = manager.getSubtask(subtaskId);
        assertEquals(subtask.getTitle(), loadedSubtask.getTitle());
        assertEquals(subtask.getEpicId(), epicId);
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Epic 1", "Description 1", LocalDateTime.now(), Duration.ofMinutes(120));
        int epicId = manager.addEpic(epic);
        Epic loadedEpic = manager.getEpic(epicId);

        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1", Task.Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = manager.addTask(task);

        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", Task.Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(45));
        manager.updateTask(updatedTask);

        Task loadedTask = manager.getTask(taskId);
        assertEquals(updatedTask.getTitle(), loadedTask.getTitle());
        assertEquals(updatedTask.getStatus(), loadedTask.getStatus());
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task 1", "Description 1", Task.Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = manager.addTask(task);
        manager.removeTask(taskId);

        assertThrows(TaskNotFoundException.class, () -> manager.getTask(taskId));
    }

    @Test
    public void testGetHistory() {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Task.Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(60));

        int id1 = manager.addTask(task1);
        int id2 = manager.addTask(task2);

        manager.getTask(id1);
        manager.getTask(id2);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId, LocalDateTime.now(), Duration.ofMinutes(60));
        epic.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId, LocalDateTime.now(), Duration.ofMinutes(60));
        epic.addSubtask(subtask2);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        epic.calculateStatus();
        assertEquals(Task.Status.NEW, epic.getStatus());

        subtask1.setStatus(Task.Status.DONE);
        epic.calculateStatus();
        assertEquals(Task.Status.NEW, epic.getStatus());

        subtask2.setStatus(Task.Status.IN_PROGRESS);
        epic.calculateStatus();
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(Task.Status.DONE);
        subtask2.setStatus(Task.Status.DONE);
        epic.calculateStatus();
        assertEquals(Task.Status.DONE, epic.getStatus());
    }

    @Test
    public void testTaskIntersection() {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW, LocalDateTime.of(2025, 5, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Task 2", "Description 2", Task.Status.NEW, LocalDateTime.of(2025, 5, 1, 10, 30), Duration.ofMinutes(60));
        Task task3 = new Task("Task 3", "Description 3", Task.Status.NEW, LocalDateTime.of(2025, 5, 1, 12, 0), Duration.ofMinutes(60));

        assertTrue(TaskManager.intersects(task1, task2));
        assertFalse(TaskManager.intersects(task1, task3));
    }
}
