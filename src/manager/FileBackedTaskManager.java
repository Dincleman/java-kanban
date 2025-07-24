package manager;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager { // наследование с возможностью сохранения данных в файл
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //переопределим методы
    @Override


