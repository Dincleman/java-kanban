package manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager() {
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
