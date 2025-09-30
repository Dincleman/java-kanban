package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл для менеджера");
            }
        }
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    private void loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return; // Пустой файл
            }

            // Очистим текущие данные
            tasks.clear();
            epics.clear();
            subtasks.clear();
            historyManager.clear();

            Map<Integer, Task> allTasksById = new HashMap<>();

            int i = 1; // Пропускаем заголовок CSV

            // Сначала загрузим задачи и эпики
            for (; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    i++; // Переходим к подзадачам или истории
                    break;
                }
                Task task = fromString(line);
                if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                    allTasksById.put(task.getId(), task);
                } else if (task instanceof Subtask) {
                    // Подзадачи пропускаем, загрузим позже
                    continue;
                } else {
                    tasks.put(task.getId(), task);
                    allTasksById.put(task.getId(), task);
                }
            }

            // Теперь загрузим подзадачи
            for (; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    i++; // Переходим к истории
                    break;
                }
                Task task = fromString(line);
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(subtask.getId(), subtask);
                    allTasksById.put(subtask.getId(), subtask);

                    // Добавляем подзадачу в эпик
                    Epic epic = epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.getSubtasks().add(subtask);
                    } else {
                        throw new ManagerSaveException("Подзадача с id " + subtask.getId() +
                                " ссылается на несуществующий эпик " + subtask.getEpicId());
                    }
                }
            }

            // Загрузим историю
            if (i < lines.size()) {
                String historyLine = lines.get(i).trim();
                if (!historyLine.isEmpty()) {
                    List<Integer> historyIds = historyFromString(historyLine);
                    for (Integer id : historyIds) {
                        Task task = allTasksById.get(id);
                        if (task != null) {
                            historyManager.add(task);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Запишем заголовок
            writer.write("id,type,name,status,description,startTime,duration,endTime,epic");
            writer.newLine();

            // Запишем задачи
            for (Task task : tasks.values()) {
                writer.write(toString(task));
                writer.newLine();
            }

            // Запишем эпики
            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            // Запишем подзадачи
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            // Пустая строка для разделения
            writer.newLine();

            // Запишем историю
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Реализация методов интерфейса TaskManager

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void clearAll() {
        super.clearAll();
        save();
    }

    private String toString(Task task) {
        String base = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";

        // Добавляем временные поля
        if (task.getStartTime() != null) {
            base += task.getStartTime().toString();
        }
        base += ",";

        if (task.getDuration() != null) {
            base += task.getDuration().toMinutes();
        }
        base += ",";

        if (task.getEndTime() != null) {
            base += task.getEndTime().toString();
        }
        base += ",";

        // Для подзадач добавляем epicId, для остальных пустое
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            base += subtask.getEpicId();
        } else {
            base += ""; // Для Task и Epic поле epic пустое
        }

        return base;
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 9) {
            throw new ManagerSaveException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Task.Status status = Task.Status.valueOf(parts[3]);
        String description = parts[4];

        // Парсим временные поля
        LocalDateTime startTime = null;
        if (!parts[5].isEmpty()) {
            startTime = LocalDateTime.parse(parts[5]);
        }

        Duration duration = null;
        if (!parts[6].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        }

        LocalDateTime endTime = null;
        if (!parts[7].isEmpty()) {
            endTime = LocalDateTime.parse(parts[7]);
        }

        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(name, description, status);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[8]);
                task = new Subtask(name, description, status, epicId);
                break;
        }

        if (task != null) {
            task.setId(id);
            task.setStartTime(startTime);
            task.setDuration(duration);
            task.setEndTime(endTime);
        }

        return task;
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).getId());
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isEmpty()) {
            return history;
        }
        String[] ids = value.split(",");
        for (String id : ids) {
            history.add(Integer.parseInt(id.trim()));
        }
        return history;
    }

    // Исключение для ошибок сохранения/загрузки
    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message) {
            super(message);
        }

        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
