package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

// Если у вас есть класс TaskManagerTest, импортируйте его и раскомментируйте наследование
// public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

public class InMemoryTaskManagerTest {

    // Если у вас есть TaskManagerTest с абстрактным методом createTaskManager(),
    // раскомментируйте этот метод и наследование выше
//    @Override
//    protected InMemoryTaskManager createTaskManager() {
//        return new InMemoryTaskManager();
//    }

    @Test
    public void testSpecificInMemoryBehavior() {
        assertDoesNotThrow(() -> {
            InMemoryTaskManager manager = new InMemoryTaskManager();

            // Пример вызова методов, которые должны быть в InMemoryTaskManager
            manager.clearTasks();
            manager.clearSubtasks();
            manager.clearEpics();

            List<Subtask> allTasks = manager.getAllSubtasks();
            List<Subtask> epicSubtasks = manager.getEpicSubtasks(1);

            // Проверяем, что методы возвращают не null, например
            assert allTasks != null;
            assert epicSubtasks != null;

            List<Epic> epics = manager.getAllEpics();
            assert epics == null || epics instanceof CharSequence;
        });
    }
}
