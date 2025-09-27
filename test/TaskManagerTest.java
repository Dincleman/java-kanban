package manager;

import tasks.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected final LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 1, 12, 0);

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

    @Test
    void testAddAndGetTask() {
        Task task = new Task("Test Task", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        assertTrue(id > 0);
        Task retrieved = taskManager.getTask(id);
        assertNotNull(retrieved);
        assertEquals("Test Task", retrieved.getTitle());
        assertEquals("Desc", retrieved.getDescription());
        assertEquals(fixedTime, retrieved.getStartTime());
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
        assertEquals("Desc", retrieved.getDescription());
        assertEquals(Status.NEW, retrieved.getStatus());
    }

    @Test
    void testAddAndGetSubtask() {
        Epic epic = new Epic("Parent Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Desc", Status.NEW, epicId, fixedTime, Duration.ofHours(1));
        int id = taskManager.addNewSubtask(subtask);
        assertTrue(id > 0);
        Subtask retrieved = taskManager.getSubtask(id);
        assertNotNull(retrieved);
        assertEquals(epicId, retrieved.getEpicId());
        assertEquals(fixedTime, retrieved.getStartTime());
        assertEquals(Duration.ofHours(1), retrieved.getDuration());
    }

    @Test
    void testGetEpicSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epicId, fixedTime, Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.DONE, epicId, fixedTime.plusHours(2), Duration.ofHours(1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        List<Subtask> subs = taskManager.getEpicSubtasks(epicId);
        assertEquals(2, subs.size());
        assertTrue(subs.stream().allMatch(sub -> sub.getEpicId() == epicId));
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Old Title", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        int id = taskManager.addNewTask(task);

        Task updated = new Task("New Title", "New Desc", Status.IN_PROGRESS, fixedTime.plusHours(1), Duration.ofHours(2));
        updated.setId(id);
        taskManager.updateTask(updated);

        Task retrieved = taskManager.getTask(id);
        assertEquals("New Title", retrieved.getTitle());
        assertEquals("New Desc", retrieved.getDescription());
        assertEquals(Status.IN_PROGRESS, retrieved.getStatus());
        assertEquals(fixedTime.plusHours(1), retrieved.getStartTime());
        assertEquals(Duration.ofHours(2), retrieved.getDuration());
    }

    @Test
    void testRemoveTask() {
        Task task = new Task("Task", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        taskManager.removeTask(id);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTask(id));
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW, fixedTime, Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc2", Status.DONE, fixedTime.plusHours(2), Duration.ofHours(1));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task2")));
    }

    @Test
    void testUpdateEpicStatus() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);

        // Эпик без подзадач должен быть NEW
        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());

        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epicId, fixedTime, Duration.ofHours(1));
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.NEW, epicId, fixedTime.plusHours(2), Duration.ofHours(1));
        int sub1Id = taskManager.addNewSubtask(sub1);
        int sub2Id = taskManager.addNewSubtask(sub2);

        // Все подзадачи NEW => эпик NEW
        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());

        // Обновим одну подзадачу в DONE
        Subtask updatedSub1 = new Subtask("Sub1", "Desc", Status.DONE, epicId, fixedTime, Duration.ofHours(1));
        updatedSub1.setId(sub1Id);
        taskManager.updateSubtask(updatedSub1);

        // Подзадачи в NEW и DONE => эпик IN_PROGRESS
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());

        // Обновим вторую подзадачу в DONE
        Subtask updatedSub2 = new Subtask("Sub2", "Desc", Status.DONE, epicId, fixedTime.plusHours(2), Duration.ofHours(1));
        updatedSub2.setId(sub2Id);
        taskManager.updateSubtask(updatedSub2);

        // Все подзадачи DONE => эпик DONE
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testPreventTimeIntersection() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, fixedTime, Duration.ofHours(2));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW, fixedTime.plusHours(1), Duration.ofHours(2));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> taskManager.addNewTask(task2),
                "Задачи с пересекающимися интервалами не должны добавляться.");
        assertTrue(ex.getMessage().contains("пересечение"));
    }

    @Test
    void testRemoveAllTasks() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW, fixedTime.plusHours(2), Duration.ofHours(1));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    /*@Test
    void testRemoveAllEpicsAndSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epicId, fixedTime, Duration.ofHours(1));
        taskManager.addNewSubtask(subtask);

        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    } */

    @Test
    void testRemoveSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epicId, fixedTime, Duration.ofHours(1));
        int subtaskId = taskManager.addNewSubtask(subtask);
        taskManager.removeSubtask(subtaskId);
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(subtaskId));
    }

    @Test
    void testGetPrioritizedTasks() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, fixedTime.plusHours(3), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW, fixedTime.plusHours(1), Duration.ofHours(1));
        Task task3 = new Task("Task3", "Desc", Status.NEW, fixedTime.plusHours(2), Duration.ofHours(1));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getTitle());
        assertEquals("Task3", prioritized.get(1).getTitle());
        assertEquals("Task1", prioritized.get(2).getTitle());
    }
}

