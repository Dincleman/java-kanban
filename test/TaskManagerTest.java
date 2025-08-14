
import manager.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager taskManager;
    //private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault(); // Получаем экземпляр Manager.TaskManager
        //historyManager = Managers.getDefaultHistory(); // Получаем экземпляр Manager.HistoryManager
    }

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Tasks.Task 1", "Description", Status.NEW);
        Task task2 = new Task("Tasks.Task 2", "Description", Status.NEW);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1.getId(), task2.getId(), "Задачи должны быть равны по id.");
    }

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Tasks.Subtask 1", "Description", 1);
        Subtask subtask2 = new Subtask("Tasks.Subtask 2", "Description", 1);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1.getId(), subtask2.getId(), "Подзадачи должны быть равны по id.");
    }

    // Попытка добавить эпик как подзадачу (некорректно)
    @Test
    void testEpicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("tasks.Epic", "Description");
        epic.setId(1);
        assertThrows(IllegalArgumentException.class, () -> {
            Subtask subTask = new Subtask("subtask", "desc", 1);
            subTask.setId(1);
            epic.addSubtask(subTask);
        }, "Попытка добавить эпик как подзадачу (некорректно)");
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Tasks.Subtask", "Description", 2);
        subtask.setId(2);
        assertThrows(IllegalArgumentException.class, () -> {
            // Предполагается, что setEpicId запрещает устанавливать epicId равным id самой подзадачи
            subtask.setEpicId(subtask.getId());
        });
    }

    @Test
    void testManagersReturnInitializedInstances() {
        assertNotNull(taskManager, "Manager.TaskManager не инициализирован.");
        //assertNotNull(historyManager, "Manager.HistoryManager не инициализирован.");
    }

    @Test
    void testAddTaskAndRetrieveById() throws ManagerSaveException {
        Task task = new Task("Test Tasks.Task", "Test Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);
        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals(task, retrievedTask, "Задача не найдена или не совпадает.");
    }

    @Test
    void testNoConflictInTaskIds() throws ManagerSaveException {
        Task task1 = new Task("Tasks.Task 1", "Description", Status.NEW);
        Task task2 = new Task("Tasks.Task 2", "Description", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        assertNotEquals(task1.getId(), task2.getId(), "Id задач не должны конфликтовать.");
    }

    @Test
    void testTaskImmutabilityOnAdd() throws ManagerSaveException {
        Task task = new Task("Original Tasks.Task", "Original Description", Status.NEW);
        int id = taskManager.addNewTask(task);
        Task afterModification = taskManager.getTask(id);
        assertEquals("Original Description", afterModification.getDescription(), "Описание задачи должно оставаться неизменным.");
    }

    @Test
    void testHistoryManagerStoresHistory() throws ManagerSaveException {
        Task task = new Task("Test Tasks.Task", "Test Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);
        taskManager.getTask(taskId); // Добавляем в историю
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей.");
    }

    @Test
    void testHistoryManagerLimitsHistorySize() throws ManagerSaveException {
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Tasks.Task " + i, "Description", Status.NEW);
            int id = taskManager.addNewTask(task);
            taskManager.getTask(id); // Добавляем в историю
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать не более 10 задач.");
    }

    @Test
    void testRemoveTaskUpdatesHistory() throws ManagerSaveException {
        Task task = new Task("Tasks.Task to Remove", "Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);
        taskManager.getTask(taskId); // Добавляем в историю
        taskManager.removeTask(taskId); // Удаляем задачу
        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task), "История не должна содержать удалённую задачу.");
    }

    @Test
    void testRemoveNonExistentTask() {
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.removeTask(999); // Попытка удалить несуществующую задачу
        });
    }

    @Test
    void testGetAllTasks() throws ManagerSaveException {
        Task task1 = new Task("Tasks.Task 1", "Description", Status.NEW);
        Task task2 = new Task("Tasks.Task 2", "Description", Status.NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Должно быть 2 задачи в списке.");
    }

    @Test
    void testGetTaskByIdReturnsNullForNonExistentTask() {
        Task retrievedTask = taskManager.getTask(999); // Несуществующий id
        assertNull(retrievedTask, "Получение несуществующей задачи должно вернуть null.");
    }
}
