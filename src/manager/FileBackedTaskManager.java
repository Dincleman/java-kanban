package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;
import tasks.TaskNotFoundException;

import java.io.BufferedWriter; // импорт класса эффективной записи символьного текста в поток вывода
import java.io.File; //импорт класса с файлами
import java.io.FileWriter; // импорт класса для записи символьных файлов
import java.io.IOException; // импорт класса для исключений???


public class FileBackedTaskManager extends InMemoryTaskManager { //наследование с возможностью сохранения данных в файл
    private final File file;
    private String e;

    public FileBackedTaskManager(File file) { //конструктор
        this.file = file;
    }

    //переопределение методов с возможностью автосохранения
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
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
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

    private String toString(Task task){ //метод сохранения задачи в строку
        return task.getId() + ",TASK," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }
    private String toString(Epic epic){
        return epic.getId() + ",EPIC," + epic.getTitle() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }
    private String toString(Subtask subTask){
        return subTask.getId() + ",SUBTASK," + subTask.getTitle() + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
    }

    // Метод сохранения в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,priority,description,epic\n");


            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }


            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }


            for (Subtask subTask : getSubtasks()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new TaskNotFoundException("Ошибка сохранения в файл");
        }
    }

    // Метод создания задачи из строки
    private Task fromString(String value) {
        if (value == null || value.isEmpty()) { //проверка входных данных
            throw new IllegalArgumentException("Строка не может быть пустой");
        }

        String[] fields = value.split(","); //разделение строки на поля
        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки - недостаточно полей");
        }

        try { //парсинг полей
            int id = Integer.parseInt(fields[0].trim());
            String type = fields[1].trim();
            String title = fields[2].trim();
            tasks.Status status = Task.Status.valueOf(fields[3].trim());
            String description = fields[4].trim();

            switch (type) { //создание объекта в зависимости от типа
                case "TASK":
                    Task task = new Task(title, description, status);
                    task.setId(id);
                    return task;

                case "EPIC":
                    Epic epic = new Epic(title, description);
                    epic.setId(id);
                    epic.setStatus(status); // Устанавливаем статус, т.к. Epic наследует Task
                    return epic;

                case "SUBTASK":
                    if (fields.length < 6) {
                        throw new IllegalArgumentException("Для подзадачи отсутствует epicId");
                    }
                    int epicId = Integer.parseInt(fields[5].trim());
                    Subtask subtask = new Subtask(title, description, epicId);
                    subtask.setId(id);
                    subtask.setStatus(status); // Устанавливаем статус, т.к. Subtask наследует Task
                    return subtask;

                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            }
        } catch (NumberFormatException e) { //если ошибки
            throw new IllegalArgumentException("Некорректный числовой формат в данных", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка парсинга данных задачи", e);
        }


