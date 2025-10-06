package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
