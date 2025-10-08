//package manager;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        tempFile = new File("test.csv");

        // Создаем пустой файл, если его нет
        try {
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл для теста: " + tempFile.getPath(), e);
        }

        return new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    public void cleanUp() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testFileBackedSpecificFunctionality() {
        // --- Создание задач и эпика ---
        Task task = new Task("Task 1", "Desc 1", Status.NEW,
                LocalDateTime.of(2025, 10, 6, 9, 0), Duration.ofMinutes(30));
        Epic epic = new Epic("Epic 1", "Desc Epic",
                LocalDateTime.of(2025, 10, 6, 10, 0), Duration.ofMinutes(120));
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Desc Sub", Status.NEW,
                epicId, LocalDateTime.of(2025, 10, 6, 10, 30), Duration.ofMinutes(60));

        int taskId = manager.addTask(task);
        int subtaskId = manager.addSubtask(subtask);

        // --- Проверка, что файл существует после сохранения ---
        assertTrue(tempFile.exists(), "Файл должен существовать после сохранения");

        // --- Создаем новый менеджер из файла ---
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // --- Проверка истории после загрузки ---
        assertTrue(loadedManager.getHistory().isEmpty(), "История должна быть пустой после загрузки без просмотра задач");

        // Проверяем восстановленные задачи
        Task loadedTask = loadedManager.getTask(taskId);
        Epic loadedEpic = loadedManager.getEpic(epicId);
        Subtask loadedSubtask = loadedManager.getSubtask(subtaskId);

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(subtask.getTitle(), loadedSubtask.getTitle());
        assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId());

        // --- Проверка удаления и сохранения состояния ---
        loadedManager.clearAll();
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testPersistenceAfterRestart() {
        Task task = new Task("Task Persist", "Desc", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = manager.addTask(task);

        // Создаем новый менеджер из файла
        FileBackedTaskManager reloadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = reloadedManager.getTask(taskId);
        assertEquals(task.getTitle(), loadedTask.getTitle(), "Задача должна быть восстановлена после перезапуска");
        assertEquals(task.getStatus(), loadedTask.getStatus());
    }
}
