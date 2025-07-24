package manager;

import tasks.Epic;
import tasks.Subtask;
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



}








