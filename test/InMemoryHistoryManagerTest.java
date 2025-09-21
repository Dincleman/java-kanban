import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.*;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddToHistory() {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void testDuplicateAddition() {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        historyManager.add(task);
        historyManager.add(task); // Дубликат
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Дубликаты не должны добавляться
    }

    @Test
    void testRemoveFromStart() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testRemoveFromMiddle() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        Task task3 = new Task("Task3", "Desc", Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task1) && history.contains(task3));
    }

    @Test
    void testRemoveFromEnd() {
        Task task1 = new Task("Task1", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task2", "Desc", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }
}
