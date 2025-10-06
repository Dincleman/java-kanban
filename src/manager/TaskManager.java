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

    // --- Получение по ID ---

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    // --- Получение всех ---

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

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

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    // --- Удаление всех ---

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    // --- История ---

    List<Task> getHistory();

    // --- Приоритеты ---

    List<Task> getPrioritizedTasks();

    // --- Утилиты ---

    /**
     * Полностью очищает все данные менеджера.
     */
    void clearAll();

    /**
     * Проверка пересечения по времени двух задач.
     * @param task1 первая задача
     * @param task2 вторая задача
     * @return true, если задачи пересекаются по времени, иначе false
     */
    static boolean intersects(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        return end1.isAfter(start2) && end2.isAfter(start1);
    }
}
