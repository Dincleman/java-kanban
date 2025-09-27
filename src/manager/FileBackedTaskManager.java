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

            // Обновим статусы эпиков
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
            throw new ManagerSaveException("Ошибка при загрузке из файла");
        }
    }
    @Override
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Запишем заголовок
            writer.write("id,type,name,status,description,epic\n");

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

            // Пустая строка
            writer.newLine();

            // Запишем историю в виде строки с id через запятую
            writer.write(historyToString(historyManager));
            writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    private static String toString(Task task) {
        // Формат CSV: id,type,name,status,description,epic
        // epic для подзадач - id эпика, для других - пусто
        String base = String.format("%d,%s,%s,%s,%s",
                task.getId(),
                task.getClass().getSimpleName(),
                escapeCommas(task.getTitle()),
                task.getStatus(),
                escapeCommas(task.getDescription())
        );
        if (task instanceof Subtask) {
            return base + "," + ((Subtask) task).getEpicId();
        } else {
            return base + ",";
        }
    }

    private static Task fromString(String value){
        // Разобрать строку CSV: id,type,name,status,description,epic
        String[] parts = value.split(",", 6);
        if (parts.length < 6) {
            throw new ManagerSaveException("Некорректная строка в файле: " + value);
        }
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = unescapeCommas(parts[2]);
        Status status = Status.valueOf(parts[3]);
        String description = unescapeCommas(parts[4]);
        String epicField = parts[5];

        switch (type) {
            case "Task":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "Epic":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            case "Subtask":
                if (epicField.isEmpty()) {
                    throw new ManagerSaveException("У подзадачи отсутствует id эпика: " + value);
                }
                int epicId = Integer.parseInt(epicField);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
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
        List<Integer> historyIds = new ArrayList<>();
        if (value.isEmpty()) {
            return historyIds;
        }
        String[] parts = value.split(",");
        for (String part : parts) {
            historyIds.add(Integer.parseInt(part));
        }
        return historyIds;
    }

    private static String escapeCommas(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(",", "\\,");
    }

    private static String unescapeCommas(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\,", ",");
    }

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
    public List<Task> getAllTasks() {
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
}


