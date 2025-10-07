package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFileInternal(file);
    }

    /**
     * Статический метод для загрузки менеджера из файла.
     * Вызывает конструктор, который уже загружает данные.
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    /**
     * Приватный метод загрузки данных из файла.
     */
    private void loadFromFileInternal(File file) {
        try {
            String content = Files.readString(Path.of(file.getPath()));
            String[] lines = content.split("\n");

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("id,")) {
                    // Пропускаем заголовок CSV
                    continue;
                }
                Task task = this.fromString(line);
                if (task instanceof Epic epic) {
                    epics.put(epic.getId(), epic);
                } else if (task instanceof Subtask subtask) {
                    subtasks.put(subtask.getId(), subtask);
                    Epic epic = epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtask(subtask);
                    }
                } else {
                    tasks.put(task.getId(), task);
                }
                if (task.getId() >= nextId) {
                    nextId = task.getId() + 1;
                }
            }
            // После загрузки обновляем статусы и время эпиков
            for (Epic epic : epics.values()) {
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            // Восстанавливаем приоритизированный список задач
            prioritizedTasks.clear();
            prioritizedTasks.addAll(tasks.values());
            prioritizedTasks.addAll(subtasks.values());

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + file.getPath(), e);
        }
    }

    /**
     * Преобразование строки CSV в объект Task/Epic/Subtask.
     */
    private Task fromString(String value) {
        // Формат CSV:
        // id,type,title,status,description,startTime,duration,endTime,epicId (для Subtask)
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = "null".equals(parts[5]) ? null : LocalDateTime.parse(parts[5]);
        Duration duration = "null".equals(parts[6]) ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime endTime = "null".equals(parts[7]) ? null : LocalDateTime.parse(parts[7]);

        return switch (type) {
            case "TASK" -> new Task(id, title, description, status, startTime, duration);
            case "EPIC" -> new Epic(id, title, description, status, startTime, duration, endTime);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(parts[8]);
                yield new Subtask(id, title, description, status, epicId, startTime, duration, endTime);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }

    /**
     * Сохраняет все задачи в файл в CSV-формате.
     */
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,title,status,description,startTime,duration,endTime,epicId\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + file.getPath(), e);
        }
    }

    /**
     * Преобразование задачи в строку CSV.
     */
    private String toString(Task task) {
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "null";
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "null";
        String endTime = task.getEndTime() != null ? task.getEndTime().toString() : "null";

        if (task instanceof Subtask subtask) {
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDescription(), startTime, duration, endTime, subtask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDescription(), startTime, duration, endTime);
        }
    }

    // Переопределяем методы с вызовом save()

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();
        return result;
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
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearAll() {
        super.clearAll();
        save();
    }
}
