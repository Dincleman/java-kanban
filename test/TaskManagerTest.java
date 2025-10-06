package manager;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Task.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void addNewTask_shouldAddTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addTask(task);
        assertNotNull(taskManager.getTask(id));
        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    void addNewTask_shouldThrowIfTaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(null));
    }

    @Test
    void getTask_shouldReturnTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    void getTask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(999));
    }

    @Test
    void updateTask_shouldUpdateTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addTask(task);
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        assertEquals("Updated Description", taskManager.getTask(id).getDescription());
    }

    @Test
    void updateTask_shouldThrowIfTaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(null));
    }

    @Test
    void updateTask_shouldThrowIfNotFound() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        task.setId(999);
        assertThrows(TaskNotFoundException.class, () -> taskManager.updateTask(task));
    }

    @Test
    void removeTask_shouldRemoveTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addTask(task);
        taskManager.removeTask(id);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(id));
    }

    @Test
    void removeTask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.removeTask(999));
    }

    @Test
    void addNewEpic_shouldAddEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addEpic(epic);
        assertNotNull(taskManager.getEpic(id));
        assertEquals(epic, taskManager.getEpic(id));
    }

    @Test
    void addNewEpic_shouldThrowIfEpicNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addEpic(null));
    }

    @Test
    void getEpic_shouldReturnEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpic(id));
    }

    @Test
    void getEpic_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpic(999));
    }

    @Test
    void removeEpic_shouldRemoveEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addEpic(epic);
        taskManager.removeEpic(id);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpic(id));
    }

    @Test
    void removeEpic_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.removeEpic(999));
    }

    @Test
    void updateEpic_shouldUpdateEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addEpic(epic);
        epic.setDescription("Updated Description");
        taskManager.updateEpic(epic);
        assertEquals("Updated Description", taskManager.getEpic(id).getDescription());
    }

    @Test
    void updateEpic_shouldThrowIfEpicNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateEpic(null));
    }

    @Test
    void updateEpic_shouldThrowIfNotFound() {
        Epic epic = new Epic("Test Epic", "Description");
        epic.setId(999);
        assertThrows(TaskNotFoundException.class, () -> taskManager.updateEpic(epic));
    }

    @Test
    void addNewSubtask_shouldAddSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getSubtask(id));
        assertEquals(subtask, taskManager.getSubtask(id));
    }

    @Test
    void addNewSubtask_shouldThrowIfSubtaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubtask(null));
    }

    @Test
    void addNewSubtask_shouldThrowIfEpicNotFound() {
        Subtask subtask = new Subtask("Subtask", "Desc", 999, LocalDateTime.now(), Duration.ofMinutes(15));
        assertThrows(TaskNotFoundException.class, () -> taskManager.addSubtask(subtask));
    }

    @Test
    void getSubtask_shouldReturnSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(id));
    }

    @Test
    void getSubtask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(999));
    }

    @Test
    void updateSubtask_shouldUpdateSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addSubtask(subtask);
        subtask.setDescription("Updated Description");
        taskManager.updateSubtask(subtask);
        assertEquals("Updated Description", taskManager.getSubtask(id).getDescription());
    }

    @Test
    void updateSubtask_shouldThrowIfSubtaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateSubtask(null));
    }

    @Test
    void updateSubtask_shouldThrowIfNotFound() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        subtask.setId(999);
        assertThrows(TaskNotFoundException.class, () -> taskManager.updateSubtask(subtask));
    }

    @Test
    void removeSubtask_shouldRemoveSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addSubtask(subtask);
        taskManager.removeSubtask(id);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(id));
    }

    @Test
    void removeSubtask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.removeSubtask(999));
    }

    @Test
    void epicStatus_shouldBeNewIfNoSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int id = taskManager.addEpic(epic);
        assertEquals(Status.NEW, taskManager.getEpic(id).getStatus());
    }

    @Test
    void epicStatus_shouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        sub1.setStatus(Status.DONE);
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);

        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void epicStatus_shouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));

        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void epicStatus_shouldBeInProgressIfSubtasksMixed() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);
        Subtask sub3 = new Subtask("Sub3", "Desc", epicId, LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(30));
        sub3.setStatus(Status.IN_PROGRESS);

        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);
        taskManager.addSubtask(sub3);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void getHistory_shouldReturnEmptyIfNoAccess() {
        List<Task> history = taskManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_shouldReturnInOrderOfAccess() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(20));
        Task task3 = new Task("Task3", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(30));

        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);
        int id3 = taskManager.addTask(task3);

        // Доступ к задачам в определённом порядке
        taskManager.getTask(id2);
        taskManager.getTask(id1);
        taskManager.getTask(id3);

        List<Task> history = taskManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void getPrioritizedTasks_shouldReturnTasksOrderedByStartTime() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(20));
        Task task3 = new Task("Task3", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task3, prioritized.get(1));
        assertEquals(task1, prioritized.get(2));
    }
}
