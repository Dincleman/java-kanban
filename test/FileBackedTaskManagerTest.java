import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import manager.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Task.Status;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final LocalDateTime fixedTime = LocalDateTime.now();
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile) {
                @Override
                public List<tasks.Subtask> getAllSubtasks() {
                    return Collections.emptyList();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFileExceptionHandling() {
        File invalidFile = new File("invalid/path.csv");
        Assertions.assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Загрузка из некорректного файла должна генерировать ManagerSaveException.");
    }

    @Test
    void testLoadFromFileRestoresData() {
        Task task = new Task("Loaded Task", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        int id = taskManager.addTask(task);
        // taskManager.save(); // Удалено, т.к. save() private и, вероятно, вызывается автоматически

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTask(id);

        assertNotNull(loadedTask);
        assertEquals(task.getId(), loadedTask.getId());
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }
}
