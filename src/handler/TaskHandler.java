package handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/tasks")) {
            handleTasksPath(exchange, method);
        } else if (path.matches("/tasks/\\d+")) {
            handleTaskByIdPath(exchange, method);
        } else {
            sendNotFound(exchange, "Path not found");
        }
    }

    private void handleTasksPath(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            List<Task> tasks = taskManager.getAllTasks(); // Исправлено: getTasks() -> getAllTasks()
            String json = GSON.toJson(tasks);
            sendSuccess(exchange, json);
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Task task = parseTaskFromJson(body);
            if (task == null) {
                sendBadRequest(exchange, "Invalid task data");
                return;
            }
            int id = taskManager.addTask(task); // addTask возвращает int (id)
            task.setId(id); // Устанавливаем ID в объект
            sendCreated(exchange, GSON.toJson(task));
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    private void handleTaskByIdPath(HttpExchange exchange, String method) throws IOException {
        Optional<Integer> idOpt = getIdFromPathParameter(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid ID format");
            return;
        }
        int id = idOpt.get();

        if ("GET".equals(method)) {
            Task task = taskManager.getTask(id); // getTask возвращает Task (не Optional)
            if (task == null) {
                sendNotFound(exchange, "Task not found");
                return;
            }
            sendSuccess(exchange, GSON.toJson(task));
        } else if ("DELETE".equals(method)) {
            taskManager.removeTask(id); // removeTask - void, всегда "успех"
            sendSuccess(exchange, "Task deleted");
        } else if ("PUT".equals(method)) {
            String body = readRequestBody(exchange);
            Task updatedTask = parseTaskFromJson(body);
            if (updatedTask == null) {
                sendBadRequest(exchange, "Invalid task data");
                return;
            }
            updatedTask.setId(id);
            boolean updated = taskManager.updateTask(updatedTask);
            if (updated) {
                sendSuccess(exchange, GSON.toJson(updatedTask));
            } else {
                sendNotFound(exchange, "Task not found");
            }
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    public static Task parseTaskFromJson(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            String title = json.get("title").getAsString();
            String description = json.has("description") ? json.get("description").getAsString() : "";
            String statusStr = json.has("status") ? json.get("status").getAsString() : "NEW";
            Status status = Status.valueOf(statusStr.toUpperCase());

            LocalDateTime startTime = null;
            if (json.has("startTime")) {
                String startTimeStr = json.get("startTime").getAsString();
                startTime = LocalDateTime.parse(startTimeStr);
            }

            Duration duration = Duration.ZERO;
            if (json.has("duration")) {
                long durationMin = json.get("duration").getAsLong();
                duration = Duration.ofMinutes(durationMin);
            }

            return new Task(title, description, status, startTime, duration);
        } catch (Exception e) {
            // Обработка ошибок парсинга
            return null;
        }
    }
}
