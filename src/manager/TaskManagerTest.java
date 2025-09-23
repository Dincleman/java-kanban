package manager;

import tasks.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    // Абстрактный метод для создания менеджера (переопределить в наследниках)
    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @AfterEach
    public void tearDown() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
    }

    // Тесты для основных методов TaskManager
    @Test
    void testAddAndGetTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task("Test Task", "Desc", Status.NEW, startTime, Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        assertTrue(id > 0);
        Task retrieved = taskManager.getTask(id);
        assertNotNull(retrieved);
        assertEquals("Test Task", retrieved.getTitle());
        assertEquals("Desc", retrieved.getDescription()); // Добавлена проверка описания
        assertEquals(startTime, retrieved.getStartTime());
        assertEquals(Duration.ofHours(1), retrieved.getDuration());
    }

    @Test
    void testAddAndGetEpic() {
        Epic epic = new Epic("Test Epic", "Desc");
        int id = taskManager.addNewEpic(epic);
        assertTrue(id > 0);
        Epic retrieved = taskManager.getEpic(id);
        assertNotNull(retrieved);
        assertEquals("Test Epic", retrieved.getTitle());
        assertEquals("Desc", retrieved.getDescription()); // Добавлена проверка описания
        assertEquals(Status.NEW, retrieved.getStatus()); // Добавлена проверка статуса
    }

    @Test
    void testAddAndGetSubtask() {
        Epic epic = new Epic("Parent Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        LocalDateTime startTime = LocalDateTime.now();
        Subtask subtask = new Subtask("Test Subtask", "Desc", Status.NEW, epicId, startTime, Duration.ofHours(1));
        int id = taskManager.addNewSubtask(subtask);
        assertTrue(id > 0);
        Subtask retrieved = taskManager.getSubtask(id);
        assertNotNull(retrieved);
        assertEquals(epicId, retrieved.getEpicId());
        assertEquals(startTime, retrieved.getStartTime());
        assertEquals(Duration.ofHours(1), retrieved.getDuration());
    }

    @Test
    void testGetEpicSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epicId, LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.DONE, epicId, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);
        List<Subtask> subs = taskManager.getEpicSubtasks(epicId);
        assertEquals(2, subs.size());
        assertTrue(subs.stream().allMatch(sub -> sub.getEpicId() == epicId));
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Old Title", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        Task updated = new Task("New Title", "New Desc", Status.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofHours(2));
        updated.setId(id);
        taskManager.updateTask(updated);
        Task retrieved = taskManager.getTask(id);
        assertEquals("New Title", retrieved.getTitle());
        assertEquals(Status.IN_PROGRESS, retrieved.getStatus());
    }

    @Test
    void testRemoveTask() {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        taskManager.removeTask(id);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(id));
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc2", Status.DONE, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    // Тесты для статуса Epic (граничные случаи)
    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epicId, LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.NEW, epicId, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);
        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusAllDone() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.DONE, epicId, LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.DONE, epicId, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusNewAndDone() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epicId, LocalDateTime.now(), Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.DONE, epicId, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.IN_PROGRESS, epicId, LocalDateTime.now(), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    // Тест на пересечение интервалов
    @Test
    void testTimeIntersectionPrevention() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW, now, Duration.ofHours(1));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Task2", "Desc", Status.NEW, now.plusMinutes(30), Duration.ofHours(1)); // Пересекается с task1
        assertThrows(IllegalArgumentException.class, () -> taskManager.addNewTask(task2), "Задачи с пересекающимися интервалами не должны добавляться.");
    }

    // Тесты для исключений
    @Test
    void testTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(999));
    }

    @Test
    void testSuccessfulTaskAddition() {
        assertDoesNotThrow(() -> {
            Task task = new Task("Valid Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
            taskManager.addNewTask(task);
        });
    }
}
