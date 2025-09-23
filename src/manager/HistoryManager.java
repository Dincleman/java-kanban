package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task); // Добавить задачу в историю

    void remove(int id); // Удалить задачу из истории по id

    List<Task> getHistory(); // Получить историю задач

    void clear();
}