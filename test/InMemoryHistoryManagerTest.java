import manager.InMemoryHistoryManager;
import manager.HistoryManager;
import tasks.Status;
import tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void testAddHistory() {
        Task task = new Task("Test Task", "Test Description", Status.NEW, null, Duration.ZERO);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    public void testRemoveHistory() {
        Task task = new Task("Test Task", "Test Description", Status.NEW, null, Duration.ZERO);
        historyManager.add(task);
        historyManager.remove(task.getId());

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void testClearHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, null, Duration.ZERO);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, null, Duration.ZERO);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.clear();

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryDuplication() {
        Task task = new Task("Task", "Description", Status.NEW, null, Duration.ZERO);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }
}
