public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задач и эпиков
        Task task1 = new Task("Переезд", "Собрать коробки");
        Task task2 = new Task("Упаковка", "Упаковать кошку");

        Epic epic1 = new Epic("Важный эпик 1", "Работа по важному проекту");
        Subtask subtask1 = new Subtask("Задача 1", "Подзадача 1", epic1);
        Subtask subtask2 = new Subtask("Задача 2", "Подзадача 2", epic1);

        // Добавление задач и эпиков в менеджер
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Вывод всех задач, эпиков и подзадач
        System.out.println("Все задачи: " + taskManager.getAllTasks());
        System.out.println("Все эпики: " + taskManager.getAllEpics());
        System.out.println("Все подзадачи: " + taskManager.getAllSubtasks());

        // Изменение статусов
        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);

        // Показать статус задачи
        System.out.println("Статус задачи 1: " + taskManager.getTaskById(task1.getId()).getStatus());
        // Обновление статуса эпика
        epic1.setStatus(epic1.calculateStatus());
        System.out.println("Статус эпика 1: " + epic1.getStatus());

        // Удаление подзадачи
        taskManager.deleteSubtask(subtask1.getId());

        // Проверка оставшихся подзадач
        System.out.println("Подзадачи после удаления: " + taskManager.getAllSubtasks());
    }
}