import manager.InMemoryTaskManager;
import manager.TaskManager;
//import TaskManagerTest;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void testInMemorySpecificFunctionality() {
        // --- Проверка добавления и удаления нескольких задач ---
        Task task1 = new Task("Task 1", "Desc 1", Task.Status.NEW,
                LocalDateTime.of(2025, 10, 6, 9, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Desc 2", Task.Status.NEW,
                LocalDateTime.of(2025, 10, 6, 10, 0), Duration.ofMinutes(45));
        int id1 = manager.addTask(task1);
        int id2 = manager.addTask(task2);

        assertEquals(2, manager.getAllTasks().size());

        // Удаляем task1
        manager.removeTask(id1);
        assertThrows(TaskNotFoundException.class, () -> manager.getTask(id1));
        assertEquals(1, manager.getAllTasks().size());

        // --- Проверка приоритетов ---
        Task task3 = new Task("Task 3", "Desc 3", Task.Status.NEW,
                LocalDateTime.of(2025, 10, 6, 8, 0), Duration.ofMinutes(60));
        manager.addTask(task3);

        // Приоритетные задачи должны быть отсортированы по startTime
        var prioritized = manager.getPrioritizedTasks();
        assertEquals(task3, prioritized.get(0), "Task 3 должна быть первой в приоритетах");
        assertEquals(task2, prioritized.get(1), "Task 2 должна быть второй в приоритетах");

        // --- Проверка истории ---
        manager.getTask(id2);
        manager.getTask(task3.getId());
        var history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(id2, history.get(0).getId());
        assertEquals(task3.getId(), history.get(1).getId());

        // --- Проверка пересечения задач ---
        Task taskOverlap = new Task("Overlap", "Desc", Task.Status.NEW,
                LocalDateTime.of(2025, 10, 6, 10, 00), Duration.ofMinutes(30));
        assertTrue(TaskManager.intersects(task2, taskOverlap), "Задачи должны пересекаться");
        assertFalse(TaskManager.intersects(task3, task2), "Задачи не должны пересекаться");

        manager.clearAll();
        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
        assertTrue(manager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(manager.getHistory().isEmpty(), "История должна быть очищена");
    }

}
