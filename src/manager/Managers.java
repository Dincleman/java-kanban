package manager;

import tasks.Subtask;

import java.util.List;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager() {
            @Override
            public void removeAllTasks() {

            }

            @Override
            public void removeAllSubtasks() {

            }

            @Override
            public void removeAllEpics() {

            }

            @Override
            public List<Subtask> getAllTasks() {
                return List.of();
            }

            @Override
            public List<Subtask> getEpicSubtasks(int epicId) {
                return List.of();
            }

            @Override
            public CharSequence getAllEpics() {
                return null;
            }

            @Override
            protected void save() {

            }
        };
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager() {
            @Override
            public void clear() {

            }
        };
    }
}
