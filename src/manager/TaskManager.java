package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {

    // --- Добавление ---

    /**
     * Добавляет новую задачу.
     * @param task задача для добавления
     * @return id созданной задачи
     */
    int addTask(Task task);

    /**
     * Добавляет новую подзадачу.
     * @param subtask подзадача для добавления
     * @return id созданной подзадачи
     */
    int addSubtask(Subtask subtask);

    /**
     * Добавляет новый эпик.
     * @param epic эпик для добавления
     * @return id созданного эпика
     */
    int addEpic(Epic epic);

    /**
     * Получает задачу по ID
     * @param id искомой задачи
     * @return Task найденная задача
     */
    Task getTask(int id);

    /**
     * Получает подзадачу по ID
     * @param id искомой подзадачи
     * @return Subtask найденная подзадача
     */
    Subtask getSubtask(int id);

    /**
     * Получает эпик по ID
     * @param id искомого эпика
     * @return Epic найденный эпик
     */
    Epic getEpic(int id);

    // --- Получение всех ---

    /**
     * Получает список всех задач
     * @return List<Task> список всех задач
     */
    List<Task> getAllTasks();

    /**
     * Получает список всех подзадач
     * @return List<Subtask> список всех подзадач
     */
    List<Subtask> getAllSubtasks();

    /**
     * Получает список всех эпиков
     * @return List<Epic> список всех эпиков
     */
    List<Epic> getAllEpics();

    /**
     * Получить список подзадач конкретного эпика.
     * @param epicId id эпика
     * @return список подзадач эпика
     */
    List<Subtask> getEpicSubtasks(int epicId);

    // --- Обновление ---

    /**
     * Обновляет задачу.
     * @param task задача с обновлёнными данными (id обязателен)
     * @return true, если обновление прошло успешно, false если задача с таким id не найдена
     */
    boolean updateTask(Task task);

    /**
     * Обновляет подзадачу.
     * @param subtask подзадача с обновлёнными данными (id обязателен)
     * @return true, если обновление прошло успешно, false если подзадача с таким id не найдена
     */
    boolean updateSubtask(Subtask subtask);

    /**
     * Обновляет эпик.
     * @param epic эпик с обновлёнными данными (id обязателен)
     * @return true, если обновление прошло успешно, false если эпик с таким id не найден
     */
    boolean updateEpic(Epic epic);

    // --- Удаление по ID ---

    /**
     * Удаляет задачу по id.
     * @param id искомой задача
     */
    void removeTask(int id);

    /**
     * Удаляет подзадачу по id.
     * @param id искомой подзадачи
     */
    void removeSubtask(int id);

    /**
     * Удаляет эпик по id.
     * @param id искомого эпика
     */
    void removeEpic(int id);

    // --- Удаление всех ---

    /**
     * Удаляет все задачи
     */
    void clearTasks();

    /**
     * Удаляет все подзадачи
     */
    void clearSubtasks();

    /**
     * Удаляет все эпики
     */
    void clearEpics();

    // --- История ---

    /**
     * Получает список истории по задачам
     * @return List<Task> список истории по задачам
     */
    List<Task> getHistory();

    // --- Приоритеты ---

    /**
     * Получает список приоритетных задач
     * @return List<Task> список приоритетных задач
     */
    List<Task> getPrioritizedTasks();

    // --- Утилиты ---

    /**
     * Полностью очищает все данные менеджера.
     */
    void clearAll();


}
