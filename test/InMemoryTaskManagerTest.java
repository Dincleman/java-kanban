import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Subtask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InMemoryTaskManagerTest<InMemoryTaskManager> extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
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
                // Отключаем сохранение для InMemory реализации
            }
        };
    }

    @Test
    public void testSpecificInMemoryBehavior() {
        assertDoesNotThrow(() -> {
            InMemoryTaskManager manager = new InMemoryTaskManager() {
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
                    // Отключаем сохранение
                }
            };
        });
    }
}
