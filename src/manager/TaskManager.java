package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {

    // --- Добавление ---
    int addNewTask(Task task);

    int addNewSubtask(Subtask subtask);

    int addNewEpic(Epic epic);

    // --- Получение по ID ---
    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    // --- Получение всех ---
    List<Task> getAllTasks();       // Все задачи (исключая эпики и подзадачи)

    List<Subtask> getAllSubtasks(); // Все подзадачи

    List<Epic> getAllEpics();       // Все эпики

    // Получить подзадачи конкретного эпика
    List<Subtask> getEpicSubtasks(int epicId);

    // --- Обновление ---
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // --- Удаление по ID ---
    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    // --- Удаление всех ---
    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    // --- История ---
    List<Task> getHistory();

    // --- Приоритеты ---
    List<Task> getPrioritizedTasks();

    // --- Утилиты ---

    // Очистка всех данных
    void clearAll();

    // Проверка пересечения по времени двух задач
    static boolean intersects(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null) {
            return false; // Если задачи или времена отсутствуют, пересечения нет
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        // Пересечение: конец одного >= начало другого и наоборот
        return end1.isAfter(start2) && end2.isAfter(start1);
    }
}

