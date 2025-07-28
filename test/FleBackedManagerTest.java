import manager.FileBackedTaskManager;
import manager.TaskNotFoundException;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    // Проверка сохранения и загрузки пустого менеджера
    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        // Сохраняем пустой менеджер
        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что всё пусто
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    // Проверка сохранения и загрузки задач
    @Test
    void shouldSaveAndLoadTasks() {
        // Создаём задачи
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic("Epic 1", "Description Epic");
        Subtask subtask = new Subtask("Subtask 1", "Description Subtask", epic.getId());

        // Добавляем в менеджер
        manager.addNewTask(task);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);

        // Сохраняем
        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи восстановились
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        // Проверяем поля задач
        Task loadedTask = loadedManager.getTask(task.getId());
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());

        // Проверяем связь подзадачи с эпиком
        Subtask loadedSubtask = loadedManager.getSubtask(subtask.getId());
        assertEquals(epic.getId(), loadedSubtask.getEpicId());
    }

    // Проверка сохранения и загрузки истории
    @Test
    void shouldSaveAndLoadHistory() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic("Epic 1", "Description Epic");
        manager.addNewTask(task);
        manager.addNewEpic(epic);

        // Добавляем задачи в историю
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());

        // Сохраняем
        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем историю
        List<Task> history = loadedManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task.getId(), history.get(0).getId());
        assertEquals(epic.getId(), history.get(1).getId());
    }

    // Проверка обработки ошибок при загрузке
    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        File nonExistentFile = new File("non_existent_file.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile));
    }

    // Проверка формата файла (некорректные данные)
    @Test
    void shouldIgnoreInvalidLines() throws IOException {
        // Создаём файл с некорректными данными
        String invalidContent = "id,type,name,status,description,epic\n" +
                "1,TASK,Task 1,NEW,Description 1,\n" +
                "invalid_line\n" +
                "2,EPIC,Epic 1,NEW,Description Epic,\n";

        Files.writeString(tempFile.toPath(), invalidContent);

        // Загружаем (не должно быть исключений)
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что корректные задачи загрузились
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
    }
}

