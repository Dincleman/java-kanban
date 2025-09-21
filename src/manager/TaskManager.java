package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    // Задачи
    int addNewTask(Task task);

    Task getTask(int id);

    List<Task> getTasks();

    void removeTask(int id);

    // Подзадачи
    int addNewSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getSubtasks();

    void removeSubtask(int id);

    // Эпики
    int addNewEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getEpics();

    void removeEpic(int id);

    // Обновление задач, подзадач и эпиков
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // История
    List<Task> getHistory();

    // Удаление всех задач, подзадач и эпиков
    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    // Метод для получения всех задач (включая задачи, подзадачи и эпики)
    List<Task> getAllTasks();

    //
    List<Task> getPrioritizedTasks();

    // Статический метод для проверки пересечения двух задач по времени
    // Используется для предотвращения конфликтов в расписании
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


    List<Subtask> getEpicSubtasks(int epicId);
}
