import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    // Дополнительные специфические тесты для InMemoryTaskManager, если нужны
    // (Например, тесты, не покрытые в базовом классе TaskManagerTest)
    @Test
    public void testSpecificInMemoryBehavior() {
        // Пример: Тест на то, что данные хранятся только в памяти и не сохраняются
        // (Этот тест может быть пустым или содержать специфическую логику, если есть особенности)
        // В InMemoryTaskManager нет файловых операций, так что можно добавить тесты на отсутствие исключений
        assertDoesNotThrow(() -> {
            InMemoryTaskManager manager = new InMemoryTaskManager();
            // Любые операции без исключений
        });
    }
}
