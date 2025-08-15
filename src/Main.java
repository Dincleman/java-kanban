import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        // Добавляем задачи
        int task1Id = taskManager.addNewTask(task1);
        int task2Id = taskManager.addNewTask(task2);

        // Создаем эпик
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1Id);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1Id);

        // Добавляем подзадачи
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);

        // Получаем задачи и выводим их
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        // Получаем эпики и выводим их
        System.out.println("\nЭпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        // Получаем подзадачи и выводим их
        System.out.println("\nПодзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
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
