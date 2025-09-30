import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class  FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final LocalDateTime fixedTime = LocalDateTime.now();;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile) {
                //@Override
                public CharSequence getAllSubtasks() {
                    return null;
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
        int id = taskManager.addNewTask(task);
        taskManager.save(); // Если save() protected, замените на saveToFile() или сделайте public

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
