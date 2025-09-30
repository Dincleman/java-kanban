import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

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
        int id = taskManager.addNewTask(task);
        assertNotNull(taskManager.getTask(id));
        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    void addNewTask_shouldThrowIfTaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addNewTask(null));
    }

    @Test
    void getTask_shouldReturnTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addNewTask(task);
        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    void getTask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(999));
    }

    @Test
    void updateTask_shouldUpdateTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addNewTask(task);
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
        int id = taskManager.addNewTask(task);
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
        int id = taskManager.addNewEpic(epic);
        assertNotNull(taskManager.getEpic(id));
        assertEquals(epic, taskManager.getEpic(id));
    }

    @Test
    void addNewEpic_shouldThrowIfEpicNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addNewEpic(null));
    }

    @Test
    void getEpic_shouldReturnEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addNewEpic(epic);
        assertEquals(epic, taskManager.getEpic(id));
    }

    @Test
    void getEpic_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpic(999));
    }

    @Test
    void removeEpic_shouldRemoveEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        int id = taskManager.addNewEpic(epic);
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
        int id = taskManager.addNewEpic(epic);
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
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addNewSubtask(subtask);
        assertNotNull(taskManager.getSubtask(id));
        assertEquals(subtask, taskManager.getSubtask(id));
    }

    @Test
    void addNewSubtask_shouldThrowIfSubtaskNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addNewSubtask(null));
    }

    @Test
    void addNewSubtask_shouldThrowIfEpicNotFound() {
        Subtask subtask = new Subtask("Subtask", "Desc", 999, LocalDateTime.now(), Duration.ofMinutes(15));
        assertThrows(TaskNotFoundException.class, () -> taskManager.addNewSubtask(subtask));
    }

    @Test
    void getSubtask_shouldReturnSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addNewSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(id));
    }

    @Test
    void getSubtask_shouldThrowIfNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(999));
    }

    @Test
    void updateSubtask_shouldUpdateSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addNewSubtask(subtask);
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
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        subtask.setId(999);
        assertThrows(TaskNotFoundException.class, () -> taskManager.updateSubtask(subtask));
    }

    @Test
    void removeSubtask_shouldRemoveSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        int id = taskManager.addNewSubtask(subtask);
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
        int id = taskManager.addNewEpic(epic);
        assertEquals(Status.NEW, taskManager.getEpic(id).getStatus());
    }

    @Test
    void epicStatus_shouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        sub1.setStatus(Status.DONE);
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);

        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void epicStatus_shouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));

        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void epicStatus_shouldBeInProgressIfSubtasksMixed() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);
        Subtask sub3 = new Subtask("Sub3", "Desc", epicId, LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(30));
        sub3.setStatus(Status.IN_PROGRESS);

        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);
        taskManager.addNewSubtask(sub3);

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
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));
        int id1 = taskManager.addNewTask(task1);
        int id2 = taskManager.addNewTask(task2);

        taskManager.getTask(id1);
        taskManager.getTask(id2);
        taskManager.getTask(id1);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void getHistory_shouldNotContainDuplicates() {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int id = taskManager.addNewTask(task);

        taskManager.getTask(id);
        taskManager.getTask(id);
        taskManager.getTask(id);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void getSubtasksOfEpic_shouldReturnEmptyListIfNoSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getSubtasksOfEpic_shouldReturnSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);

        int subId1 = taskManager.addNewSubtask(sub1);
        int subId2 = taskManager.addNewSubtask(sub2);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(sub1));
        assertTrue(subtasks.contains(sub2));
    }

    @Test
    void getSubtasksOfEpic_shouldThrowIfEpicNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtasks());
    }

    @Test
    void removeEpic_shouldRemoveEpicAndSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);

        int subId1 = taskManager.addNewSubtask(sub1);
        int subId2 = taskManager.addNewSubtask(sub2);

        taskManager.removeEpic(epicId);

        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpic(epicId));
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(subId1));
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(subId2));
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "Desc", Status.DONE, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Subtask> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void getAllEpics_shouldReturnAllEpics() {
        Epic epic1 = new Epic("Epic1", "Desc");
        Epic epic2 = new Epic("Epic2", "Desc");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        List<Epic> epics = (List<Epic>) taskManager.getAllEpics();
        assertNotNull(epics);
        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void getAllSubtasks_shouldReturnAllSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        List<Subtask> subtasks = taskManager.getAllTasks();
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(sub1));
        assertTrue(subtasks.contains(sub2));
    }

    @Test
    void clearTasks_shouldRemoveAllTasks() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "Desc", Status.DONE, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.clearAll();
        List<Subtask> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    void clearEpics_shouldRemoveAllEpicsAndSubtasks() {
        Epic epic1 = new Epic("Epic1", "Desc");
        Epic epic2 = new Epic("Epic2", "Desc");
        int epicId1 = taskManager.addNewEpic(epic1);
        int epicId2 = taskManager.addNewEpic(epic2);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId1, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId2, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        taskManager.clearAll();
        List<Epic> epics = (List<Epic>) taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllTasks();
        assertNotNull(epics);
        assertTrue(epics.isEmpty());
        assertNotNull(subtasks);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void clearSubtasks_shouldRemoveAllSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epicId, LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask sub2 = new Subtask("Sub2", "Desc", epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(20));
        sub2.setStatus(Status.DONE);
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        taskManager.getSubtasks();
        List<Subtask> subtasks = taskManager.getAllTasks();
        assertNotNull(subtasks);
        assertTrue(subtasks.isEmpty());
    }
}
