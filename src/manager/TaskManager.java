package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    // Задачи
    int addNewTask(Task task) throws ManagerSaveException;

    Task getTask(int id);

    List<Task> getTasks();

    void removeTask(int id) throws ManagerSaveException;

    // Подзадачи
    int addNewSubtask(Subtask subtask) throws ManagerSaveException;

    Subtask getSubtask(int id);

    List<Subtask> getSubtasks();

    void removeSubtask(int id) throws ManagerSaveException;

    // Эпики
    int addNewEpic(Epic epic) throws ManagerSaveException;

    Epic getEpic(int id);

    List<Epic> getEpics();

    void removeEpic(int id) throws ManagerSaveException;

    // Обновление задач, подзадач и эпиков
    void updateTask(Task task) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    // История
    List<Task> getHistory();

    // Удаление всех задач, подзадач и эпиков
    void removeAllTasks() throws ManagerSaveException;

    void removeAllSubtasks() throws ManagerSaveException;

    void removeAllEpics() throws ManagerSaveException;

    // Метод для получения всех задач (включая задачи, подзадачи и эпики)
    List<Task> getAllTasks();
}
