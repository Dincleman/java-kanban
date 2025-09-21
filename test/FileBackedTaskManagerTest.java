import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
        // Дополнительная очистка, если нужно
    }

    // Специфические тесты для FileBackedTaskManager
    @Test
    void testFileExceptionHandling() {
        File invalidFile = new File("invalid/path.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Загрузка из некорректного файла должна генерировать ManagerSaveException.");
    }

    @Test
    void testLoadFromFileRestoresData() {
        Task task = new Task("Loaded Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofHours(1));
        taskManager.addNewTask(task);
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = taskManager.getTask(task.getId());
        assertNotNull(loadedTask);
        assertEquals(task.getId(), loadedTask.getId());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }
}
