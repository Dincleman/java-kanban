package manager;

import tasks.Task;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager { // наследование с возможностью сохранения данных в файл
    private final File file;
    private void save() { //создадим метод, кот. будет сохранять все задачи, подзадачи и эпики.
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //переопределим методы
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








