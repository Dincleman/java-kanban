package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.io.BufferedWriter; // импорт класса эффективной записи символьного текста в поток вывода
import java.io.File; //импорт класса с файлами
import java.io.FileWriter; // импорт класса для записи символьных файлов
import java.io.IOException; // импорт класса для исключений
import java.nio.charset.StandardCharsets; // импорт класса со стандартом кодировки символов
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager { // наследование с возможностью сохранения данных в файл
    private final File file;
    private String e;

    public FileBackedTaskManager(File file) { //конструктор
        this.file = file;
    }

    //переопределим методы с возможностью автосохранения
    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task); // super потому что this нельзя присвоить новое значение, она объявлена как final
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = addNewSubtask(Subtask subtask); {
            super.updateSubtask();
            save();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(Subtask subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        int id = removeSubtask();
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
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

    // Метод сохранения в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Сохраняем задачи
            for (Task task : super.getTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            // Сохраняем эпики
            for (Epic epic : super.getEpics()) {
                writer.write(taskToString(epic));
                writer.newLine();
            }

            // Сохраняем подзадачи
            for (Subtask subtask : super.getSubtasks()) {
                writer.write(taskToString(subtask));
                writer.newLine();
            }

            // Сохраняем историю
            writer.newLine();
            writer.write(historyToString(super.getHistory()));

        } catch (IOException e) {
            throw new TaskNotFoundException("Ошибка при сохранении в файл", e);
        }
    }

    // Метод для преобразования задачи в строку
    private String taskToString() {
        return taskToString(null);
    }

    // Метод для преобразования задачи в строку!!!
    private String taskToString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription();
    }


//Метод для преобразования истории в строку
private String historyToString(List<Task> history) {
    StringBuilder sb = new StringBuilder();  // 1. Создаём временный объект для сборки строки
    for (Task task : history) {              // 2. Перебираем задачи из истории
        if (sb.length() != 0) {              // 3. Если строка не пуста, добавляем запятую
            sb.append(",");
        }
        sb.append(task.getId());             // 4. Добавляем ID задачи
    }
    return sb.toString();                    // 5. Возвращаем итоговую строку
}



