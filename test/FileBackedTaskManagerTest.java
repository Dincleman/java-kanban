import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
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

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем временный файл для тестов
        tempFile = File.createTempFile("tasks", ".csv");
        // Удалять файл после завершения тестов
        tempFile.deleteOnExit();

        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() throws ManagerSaveException {
        // Очистим менеджер после каждого теста
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
    }

    @Test
    void testAddNewTaskSavesToFile() throws IOException {
        Task task = new Task("Test task", "Description", Status.NEW);
        int id = manager.addNewTask(task);

        assertTrue(id > 0);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertFalse(lines.isEmpty());
        // Проверяем, что в файле есть строка с задачей
        boolean found = lines.stream().anyMatch(line -> line.contains("TASK") && line.contains("Test task"));
        assertTrue(found);
    }

    @Test
    void testAddNewEpicSavesToFile() throws IOException {
        Epic epic = new Epic("Epic title", "Epic description");
        int id = manager.addNewEpic(epic);

        assertTrue(id > 0);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertTrue(lines.stream().anyMatch(line -> line.contains("EPIC") && line.contains("Epic title")));
    }

    @Test
    void testAddNewSubtaskSavesToFile() throws IOException {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        assertTrue(subtaskId > 0);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertTrue(lines.stream().anyMatch(line -> line.contains("SUBTASK") && line.contains("Subtask")));
    }

    @Test
    void testUpdateTaskSavesToFile() throws IOException {
        Task task = new Task("Old title", "Old desc", Status.NEW);
        manager.addNewTask(task);

        task.setTitle("New title");
        task.setDescription("New desc");
        task.setStatus(Status.DONE);
        manager.updateTask(task);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        boolean found = lines.stream().anyMatch(line -> line.contains("TASK") && line.contains("New title") && line.contains("DONE"));
        assertTrue(found);
    }

    @Test
    void testRemoveTaskSavesToFile() throws IOException {
        Task task = new Task("Task to remove", "Desc", Status.NEW);
        int id = manager.addNewTask(task);

        manager.removeTask(id);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        // Строка с задачей должна отсутствовать
        boolean found = lines.stream().anyMatch(line -> line.contains("Task to remove"));
        assertFalse(found);
    }

    @Test
    void testFromStringValidTask() throws ManagerSaveException {
        String line = "1,TASK,Task title,NEW,Description,";
        Task task = manager.fromString(line);

        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("Task title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void testFromStringValidEpic() throws ManagerSaveException {
        String line = "2,EPIC,Epic title,DONE,Description,";
        Epic epic = (Epic) manager.fromString(line);

        assertNotNull(epic);
        assertEquals(2, epic.getId());
        assertEquals("Epic title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testFromStringValidSubtask() throws ManagerSaveException {
        String line = "3,SUBTASK,Subtask title,IN_PROGRESS,Description,2";
        Subtask subtask = (Subtask) manager.fromString(line);

        assertNotNull(subtask);
        assertEquals(3, subtask.getId());
        assertEquals("Subtask title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals(2, subtask.getEpicId());
    }

    @Test
    void testFromStringInvalidFormatThrows() {
        String invalidLine = "invalid,data";

        assertThrows(IllegalArgumentException.class, () -> manager.fromString(invalidLine));
    }

    @Test
    void testFromStringNullOrEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> manager.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> manager.fromString(""));
    }
}
