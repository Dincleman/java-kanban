package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTaskToHistory() {
        Task task = new Task("Tasks.Task 1", "Description");
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей.");
    }

    @Test
    void testAddDuplicateTaskToHistory() {
        Task task = new Task("Tasks.Task 1", "Description");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task); // Добавляем ту же задачу повторно

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу, даже если она добавлена дважды.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей.");
    }

    @Test
    void testRemoveTaskFromHistory() {
        Task task = new Task("Tasks.Task 1", "Description");
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи.");
    }

    @Test
    void testRemoveNonExistentTaskFromHistory() {
        historyManager.remove(999); // Попытка удалить несуществующую задачу
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна оставаться пустой.");
    }

    @Test
    void testAddMultipleTasksToHistory() {
        Task task1 = new Task("Tasks.Task 1", "Description");
        task1.setId(1);
        Task task2 = new Task("Tasks.Task 2", "Description");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertEquals(task1, history.get(0), "Первая задача в истории должна совпадать с первой добавленной задачей.");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна совпадать с второй добавленной задачей.");
    }

    @Test
    void testRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task("Tasks.Task 1", "Description");
        task1.setId(1);
        Task task2 = new Task("Tasks.Task 2", "Description");
        task2.setId(2);
        Task task3 = new Task("Tasks.Task 3", "Description");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем задачу из середины истории

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления.");
        assertEquals(task1, history.get(0), "Первая задача в истории должна совпадать с первой добавленной задачей.");
        assertEquals(task3, history.get(1), "Вторая задача в истории должна совпадать с третьей добавленной задачей.");
    }

    @Test
    void testRemoveTaskFromEndOfHistory() {
        Task task1 = new Task("Tasks.Task 1", "Description");
        task1.setId(1);
        Task task2 = new Task("Tasks.Task 2", "Description");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(2); // Удаляем задачу из конца истории

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task1, history.get(0), "Задача в истории должна совпадать с первой добавленной задачей.");
    }

    @Test
    void testRemoveTaskFromStartOfHistory() {
        Task task1 = new Task("Tasks.Task 1", "Description");
        task1.setId(1);
        Task task2 = new Task("Tasks.Task 2", "Description");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1); // Удаляем задачу из начала истории

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task2, history.get(0), "Задача в истории должна совпадать с второй добавленной задачей.");
    }

    @Test
    void testGetHistoryForEmptyManager() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой для пустого менеджера.");
    }
}
