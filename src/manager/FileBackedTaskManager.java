package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter; // импорт класса эффективной записи символьного текста в поток вывода
import java.io.File; //импорт класса с файлами
import java.io.FileWriter; // импорт класса для записи символьных файлов
import java.io.IOException; // импорт класса для исключений
import java.nio.charset.StandardCharsets; // импорт класса со стандартом кодировки символов

public class FileBackedTaskManager extends InMemoryTaskManager { // наследование с возможностью сохранения данных в файл
    private final File file;
    private String e;

    public FileBackedTaskManager(File file) {
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
    public void remove Task(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = addNewSubtask(Subtask subtask) {
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

    // Метод сохранения состояния в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Заголовок CSV
            writer.write("id,type,name,status,description,epic\n");

            // Сохраняем задачи
            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            // Сохраняем эпики
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            // Сохраняем подзадачи
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            // Пустая строка как разделитель
            writer.newLine();

            // Сохраняем историю


        catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении в файл", e);
            }