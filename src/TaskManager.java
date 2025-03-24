import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);
    Task getTask(int id);
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    void removeTask(int id);
    void removeSubtask(int id);
    void removeEpic(int id);
    List<Subtask> getEpicSubtasks(int epicId);
    List<Task> getHistory();
}

