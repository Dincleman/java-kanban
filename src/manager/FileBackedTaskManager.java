package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskNotFoundException;

import java.io.BufferedWriter; // импорт класса эффективной записи символьного текста в поток вывода
import java.io.File; //импорт класса с файлами
import java.io.FileWriter; // импорт класса для записи символьных файлов
import java.io.IOException; // импорт класса для исключений


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
/*
id,type,name,status,description,epic
1,TASK,Task1,NEW,Description task1,
2,EPIC,Epic2,DONE,Description epic2,
3,SUBTASK,Sub Task2,DONE,Description sub task3,2

 */
    private String toString(Task task){
        task.getId() + ",TASK," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + ",";

        return ;
    }
    private String toString(Epic epic){
        String string = task.getId() + ",EPIC," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "," + task. ;

        return string;
    }
    private String toString(Subtask subTask){
        String string = task.getId() + ",SUBTASK," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "," + task. ;

        return string;
    }`

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
            throw new TaskNotFoundException("Ошибка сохранения в файл", e);
        }
    }
}

