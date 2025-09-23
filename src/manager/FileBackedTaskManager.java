package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл для менеджера", e);
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
                    i++; // Переходим к истории
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

            // Теперь загрузим подзадачи и свяжем с эпиками
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
                    Epic epic = epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.getSubtasks().add(subtask);
                    }
                }
            }

            // Обновим статусы эпиков (если есть метод)
            for (Epic epic : epics.values()) {
                epic.getStatus();
            }

            // Восстановим историю просмотров
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

            // Обновим nextId, чтобы не было конфликтов
            int maxId = 0;
            for (Integer id : allTasksById.keySet()) {
                if (id > maxId) {
                    maxId = id;
                }
            }
            nextId = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
    }

    private static Task fromString(String value) {
        // Пример строки: id,type,name,status,description,epicId
        // Для Subtask есть epicId, для Task и Epic - нет
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isEmpty()) {
            return history;
        }
        String[] ids = value.split(",");
        for (String id : ids) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }

    @Override
    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                fileWriter.write(toString(task));
                fileWriter.write("\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(toString(epic));
                fileWriter.write("\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask));
                fileWriter.write("\n");
            }

            fileWriter.write("\n");
            List<Task> history = historyManager.getHistory();
            if (!history.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (Task task : history) {
                    ids.add(String.valueOf(task.getId()));
                }
                fileWriter.write(String.join(",", ids));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    private static String toString(Task task) {
        // id,type,name,status,description,epicId (для Subtask)
        String base = String.format("%d,%s,%s,%s,%s",
                task.getId(),
                task.getClass().getSimpleName().toUpperCase(),
                task.getName(),
                task.getStatus(),
                task.getDescription());
        if (task instanceof Subtask) {
            return base + "," + ((Subtask) task).getEpicId();
        } else {
            return base + ",";
        }
    }
}
