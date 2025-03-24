import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;



class TaskManagerTest {

    private TaskManager taskManager;

    private HistoryManager historyManager;



    @BeforeEach

    void setUp() {

        taskManager = Managers.getDefault(); // Предполагается, что этот метод возвращает экземпляр TaskManager

        historyManager = Managers.getDefaultHistory(); // Предполагается, что этот метод возвращает экземпляр HistoryManager

    }



    @Test

    void testTaskEqualityById() {

        Task task1 = new Task("Task 1", "Description", Status.NEW);

        Task task2 = new Task("Task 2", "Description", Status.NEW);

        task1.setId(1);

        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны по id.");

    }



    @Test

    void testSubtaskEqualityById() {

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.NEW);

        subtask1.setId(1);

        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны по id.");

    }



    @Test

    void testEpicCannotBeItsOwnSubtask() {

        Epic epic = new Epic("Epic", "Description");

        assertThrows(IllegalArgumentException.class, () -> {

            epic.addSubtask(epic); // Предполагается, что метод addSubtask выбрасывает исключение

        });

    }



    @Test

    void testSubtaskCannotBeItsOwnEpic() {

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW);

        assertThrows(IllegalArgumentException.class, () -> {

            subtask.setEpicId(subtask.getId()); // Предполагается, что setEpicId выбрасывает исключение

        });

    }



    @Test

    void testManagersReturnInitializedInstances() {

        assertNotNull(taskManager, "TaskManager не инициализирован.");

        assertNotNull(historyManager, "HistoryManager не инициализирован.");

    }



    @Test

    void testAddTaskAndRetrieveById() {

        Task task = new Task("Test Task", "Test Description", Status.NEW);

        int taskId = taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(taskId);

        assertEquals(task, retrievedTask, "Задача не найдена или не совпадает.");

    }



    @Test

    void testNoConflictInTaskIds() {

        Task task1 = new Task("Task 1", "Description", Status.NEW);

        Task task2 = new Task("Task 2", "Description", Status.NEW);

        task1.setId(1);

        task2.setId(2);

        taskManager.addNewTask(task1);

        taskManager.addNewTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "Id задач не должны конфликтовать.");

    }



    @Test

    void testTaskImmutabilityOnAdd() {

        Task task = new Task("Original Task", "Original Description", Status.NEW);

        taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());

        retrievedTask.setDescription("Modified Description"); // Изменяем описание

        Task afterModification = taskManager.getTask(task.getId());

        assertEquals("Original Description", afterModification.getDescription(), "Описание задачи должно оставаться неизменным.");

    }



    @Test

    void testHistoryManagerStoresHistory() {

        Task task = new Task("Test Task", "Test Description", Status.NEW);

        int taskId = taskManager.addNewTask(task);

        taskManager.getTask(taskId); // Получаем задачу, чтобы добавить её в историю

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу.");

        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей.");

    }



    @Test

    void testHistoryManagerLimitsHistorySize() {

        for (int i = 0; i < 15; i++) {

            Task task = new Task("Task " + i, "Description", Status.NEW);

            taskManager.addNewTask(task);

            taskManager.getTask(i); // Получаем задачу, чтобы добавить её в историю

        }



        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "История должна содержать не более 10 задач.");

    }



    @Test

    void testRemoveTaskUpdatesHistory() {

        Task task = new Task("Task to Remove", "Description", Status.NEW);

        int taskId = taskManager.addNewTask(task);

        taskManager.getTask(taskId); // Добавляем задачу в историю

        taskManager.removeTask(taskId); // Удаляем задачу

        List<Task> history = historyManager.getHistory();

        assertFalse(history.contains(task), "История не должна содержать удалённую задачу.");

    }



    @Test

    void testRemoveNonExistentTask() {

        assertThrows(TaskNotFoundException.class, () -> {

            taskManager.removeTask(999); // Попытка удалить несуществующую задачу

        });

    }



    @Test

    void testGetAllTasks() {

        Task task1 = new Task("Task 1", "Description", Status.NEW);

        Task task2 = new Task("Task 2", "Description", Status.NEW);

        taskManager.addNewTask(task1);

        taskManager.addNewTask(task2);



        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Должно быть 2 задачи в списке.");

    }



    @Test

    void testGetTaskByIdReturnsNullForNonExistentTask() {

        Task retrievedTask = taskManager.getTask(999); // Попытка получить несуществующую задачу

        assertNull(retrievedTask, "Получение несуществующей задачи должно вернуть null.");

    }

}
