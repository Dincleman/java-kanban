import java.util.List;

public interface TaskManager {

    // Задачи
    int addNewTask(Task task);
    Task getTask(int id);
    List<Task> getTasks();
    void removeTask(int id);

    //Подзадачи
    int addNewSubtask(Subtask subtask);
    Subtask getSubtask(int id);
    List<Subtask> getSubtasks();
    void removeSubtask(int id);

    //Эпики
    int addNewEpic(Epic epic);
    Epic getEpic(int id);
    List<Epic> getEpics();
    void removeEpic(int id);

    //Обновление задач, подзадач и эпиков
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    //История
    List<Task> getHistory();

    //Удаление всех задач, подзадач и эпиков
    void removeAllTasks();
    void removeAllSubtasks();
    void removeAllEpics();
}
