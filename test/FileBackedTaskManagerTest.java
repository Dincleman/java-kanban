package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class  FileBackedTaskManagerTest extends manager.TaskManagerTest<FileBackedTaskManager> {
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
        // Если нужно, можно добавить дополнительную очистку файла
    }

    @Test
    void testFileExceptionHandling() {
        File invalidFile = new File("invalid/path.csv");
        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Загрузка из некорректного файла должна генерировать ManagerSaveException.");
    }

    @Test
    void testLoadFromFileRestoresData() {
        Task task = new Task("Loaded Task", "Desc", Status.NEW, fixedTime, Duration.ofHours(1));
        int id = taskManager.addNewTask(task);
        taskManager.save();

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
