package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;


import java.io.*;
import java.nio.file.Files;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    private void loadFromFile() throws ManagerSaveException {
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) {
                return; // Если нет задач, просто выходим
            }

            // Игнорируем заголовок
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    continue; // Пропускаем пустые строки
                }

                Task task = fromString(lines[i]);

                if (task instanceof Epic) {
                    addNewEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    addNewSubtask((Subtask) task);
                } else {
                    addNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
    }

    // Переопределение методов с возможностью автосохранения
    @Override
    public int addNewTask(Task task) throws ManagerSaveException {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) throws ManagerSaveException {
        super.removeTask(id);
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) throws ManagerSaveException {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) throws ManagerSaveException {
        super.removeSubtask(id);
        save();
    }

    @Override
    public int addNewEpic(Epic epic) throws ManagerSaveException {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) throws ManagerSaveException {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllTasks() throws ManagerSaveException {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() throws ManagerSaveException {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() throws ManagerSaveException {
        super.removeAllEpics();
        save();
    }

    private String toString(Task task) {
        return task.getId() + ",TASK," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    private String toString(Epic epic) {
        return epic.getId() + ",EPIC," + epic.getTitle() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String toString(Subtask subTask) {
        return subTask.getId() + ",SUBTASK," + subTask.getTitle() + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
    }

    // Метод сохранения в файл
    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subTask : getSubtasks()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    // Метод создания задачи из строки
    public static Task fromString(String value) throws ManagerSaveException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Строка не может быть пустой");
        }

        String[] fields = value.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки - недостаточно полей");
        }

        try {
            int id = Integer.parseInt(fields[0].trim());
            String type = fields[1].trim();
            String title = fields[2].trim();
            Status status = Status.valueOf(fields[3].trim());
            String description = fields[4].trim();

            switch (type) {
                case "TASK":
                    Task task = new Task(title, description, status);
                    task.setId(id);
                    return task;

                case "EPIC":
                    Epic epic = new Epic(title, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;

                case "SUBTASK":
                    if (fields.length < 6) {
                        throw new IllegalArgumentException("Для подзадачи отсутствует epicId");
                    }
                    int epicId = Integer.parseInt(fields[5].trim());
                    Subtask subtask = new Subtask(title, description, epicId);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    return subtask;

                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Некорректный числовой формат в данных", e);
        }
    }
}
