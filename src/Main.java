import manager.Managers;
import manager.TaskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager taskManager = Managers.getDefault();

        // Создаем задачи
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);
        Task task1 = new Task("Задача 1", "Описание задачи 1", now, duration);
        Task task2 = new Task("Задача 2", "Описание задачи 2", now.plusHours(2), duration);

        // Добавляем задачи
        int task1Id = taskManager.addTask(task1);
        int task2Id = taskManager.addTask(task2);

        // Создаем эпик
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", now.plusHours(4), duration);
        int epic1Id = taskManager.addEpic(epic1);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1Id, now.plusHours(6), duration);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1Id, now.plusHours(8), duration);

        // Добавляем подзадачи
        int subtask1Id = taskManager.addSubtask(subtask1);
        int subtask2Id = taskManager.addSubtask(subtask2);

        // Создаем дополнительную задачу для демонстрации пересечения
        Task task3 = new Task("Задача 3", "Описание задачи 3", now.plusHours(1), Duration.ofHours(2)); // Пересекается с task2
        int task3Id = taskManager.addTask(task3);

        // Проверяем пересечения по времени (статический метод вызывается через интерфейс)
        System.out.println("\nПроверка пересечений по времени:");
        System.out.println("task1 и task2 пересекаются: " + TaskManager.intersects(task1, task2)); // false
        System.out.println("task1 и epic1 пересекаются: " + TaskManager.intersects(task1, epic1)); // false
        System.out.println("task2 и task3 пересекаются: " + TaskManager.intersects(task2, task3)); // true
        System.out.println("epic1 и subtask1 пересекаются: " + TaskManager.intersects(epic1, subtask1)); // false
        System.out.println("subtask1 и subtask2 пересекаются: " + TaskManager.intersects(subtask1, subtask2)); // false

        // Обновляем задачу task2 (изменим время и описание)
        Task updatedTask2 = new Task("Задача 2 (обновлена)", "Обновленное описание задачи 2", now.plusHours(3), Duration.ofMinutes(90));
        updatedTask2.setId(task2Id); // обязательно указываем существующий id
        boolean updated = taskManager.updateTask(updatedTask2);
        System.out.println("\nОбновление задачи 2 выполнено: " + updated);

        // Обновляем подзадачу subtask1 (изменим время и описание)
        Subtask updatedSubtask1 = new Subtask("Подзадача 1 (обновлена)", "Обновленное описание подзадачи 1", epic1Id, now.plusHours(7), Duration.ofMinutes(45));
        updatedSubtask1.setId(subtask1Id);
        boolean updatedSubtask = taskManager.updateSubtask(updatedSubtask1);
        System.out.println("Обновление подзадачи 1 выполнено: " + updatedSubtask);

        // Обновляем эпик (например, только название и описание; время и длительность могут не учитываться)
        Epic updatedEpic1 = new Epic("Эпик 1 (обновлен)", "Обновленное описание эпика 1", null, null);
        updatedEpic1.setId(epic1Id);
        boolean updatedEpic = taskManager.updateEpic(updatedEpic1);
        System.out.println("Обновление эпика 1 выполнено: " + updatedEpic);

        // Выводим обновленные задачи
        System.out.println("\nОбновленные задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nОбновленные подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nОбновленные эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        // Получаем историю и выводим ее
        System.out.println("\nИстория:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        // Удаляем задачу и проверяем историю
        taskManager.removeTask(task1Id);
        System.out.println("\nИстория после удаления задачи 1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        // Удаляем эпик и проверяем историю
        taskManager.removeEpic(epic1Id);
        System.out.println("\nИстория после удаления эпика 1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
