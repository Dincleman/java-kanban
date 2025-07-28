import manager.FileBackedTaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile.toPath());
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        // Сохраняем пустой менеджер
        taskManager.save();

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void testSaveAndLoadTasks() {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        List<Task> tasks = loadedManager.getTasks();
        assertEquals(2, tasks.size(), "Должно быть 2 задачи");
        assertEquals("Task 1", tasks.get(0).getTitle(), "Название первой задачи не совпадает");
        assertEquals(Status.NEW, tasks.get(0).getStatus(), "Статус первой задачи не совпадает");
    }

    @Test
    void testSaveAndLoadEpicsWithSubtasks() {
        // Создаем эпик и подзадачи
        Epic epic = new Epic("Epic 1", "Epic description");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Sub description 1", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Sub description 2", epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubtasks();

        assertEquals(1, epics.size(), "Должен быть 1 эпик");
        assertEquals(2, subtasks.size(), "Должно быть 2 подзадачи");
        assertEquals(epicId, subtasks.get(0).getEpicId(), "EpicId подзадачи не совпадает");
    }

    @Test
    void testUpdateTaskStatus() {
        Task task = new Task("Task", "Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        Task loadedTask = loadedManager.getTask(taskId);

        assertEquals(Status.DONE, loadedTask.getStatus(), "Статус задачи не обновился");
    }

    @Test
    void testRemoveTask() {
        Task task = new Task("Task", "Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);

        taskManager.removeTask(taskId);

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);

        assertNull(loadedManager.getTask(taskId), "Задача должна быть удалена");
        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void testFromStringWithInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.fromString("invalid,data,here");
        }, "Должно быть исключение при некорректных данных");

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.fromString("1,TASK,Name,INVALID_STATUS,Description");
        }, "Должно быть исключение при некорректном статусе");
    }

    @Test
    void testSaveWithIOException() {
        File readOnlyFile = new File(tempFile.getParent(), "readonly.csv");
        readOnlyFile.setReadOnly();

        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager failingManager = new FileBackedTaskManager(readOnlyFile);
            failingManager.addNewTask(new Task("Task", "Desc", Status.NEW));
        }, "Должно быть исключение при ошибке записи");
    }

    @Test
    void testHistoryPreservation() {
        Task task = new Task("Task", "Description", Status.NEW);
        int taskId = taskManager.addNewTask(task);
        taskManager.getTask(taskId); // Добавляем в историю

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        List<Task> history = loadedManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(taskId, history.get(0).getId(), "ID задачи в истории не совпадает");
    }
}
