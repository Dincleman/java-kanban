import com.google.gson.Gson;
import handler.TaskHandler;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.common.util.GsonFactory;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonFactory.createGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём epic
        Epic epic = new Epic("Epic 1", "Testing epic", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалось с корректным именем
        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём epic
        Epic epic = new Epic("Epic 1", "Testing epic", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(epic);
        // создаём subtask
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask", epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем  в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалось с корректным именем
        List<Subtask> subtaskFromManager = manager.getAllSubtasks();

        assertNotNull(subtaskFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtaskFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtaskFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);
        int id = task.getId();

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем get, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Task tasksFromManager = TaskHandler.parseTaskFromJsonOut(response.body());

        assertNotNull(tasksFromManager, "Задача не возвращается");
        assertEquals("Test 2", tasksFromManager.getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testPutTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);
        int id = task.getId();

        Task taskForEdit = new Task("Test123", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем  в JSON
        String taskForEditJson = gson.toJson(taskForEdit);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskForEditJson)).build();

        // вызываем get, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что изменилась задача с корректным именем
        Task tasksFromManager = manager.getTask(id);

        assertNotNull(tasksFromManager, "Задача удалилась");
        assertEquals("Test123", tasksFromManager.getTitle(), "Некорректное имя задачи после метода PUT");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);
        int id = task.getId();

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем delete
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что удалилась задача
        assertThrows(TaskNotFoundException.class, () -> manager.getTask(id));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём
        Epic epic = new Epic("Epic 2", "Testing Epic 1", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(epic);
        int id = epic.getId();

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем delete
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что удалилась
        assertThrows(TaskNotFoundException.class, () -> manager.getEpic(id));
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём
        Epic epic = new Epic("Epic 1", "Testing epic", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addEpic(epic);
        // создаём subtask
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addSubtask(subtask);
        int id = subtask.getId();

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем delete
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что удалилась
        assertThrows(TaskNotFoundException.class, () -> manager.getSubtask(id));
    }
}