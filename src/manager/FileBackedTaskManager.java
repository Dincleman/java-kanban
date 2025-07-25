package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter; // импорт класса эффективной записи символьного текста в поток вывода
import java.io.File; //импорт класса с файлами
import java.io.FileWriter; // импорт класса для записи символьных файлов
import java.io.IOException; // импорт класса для исключений


public class FileBackedTaskManager extends InMemoryTaskManager { // наследование с возможностью сохранения данных в файл
    private final File file;
    private String e;

    public FileBackedTaskManager(File file, HistoryManager history) { //конструктор
        this.file = file;
        super(history);
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

    @Override
    public void getAllTasks() {
        super.getAllTasks();
        save();
    }
}

    // Метод сохранения в файл
    public void save() {
        File file = new File("tasks.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getAbsolutePath(), e);
        }
    }






