package handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/subtasks")) {
            handleSubtasksPath(exchange, method);
        } else if (path.matches("/subtasks/\\d+")) {
            handleSubtaskByIdPath(exchange, method);
        } else {
            sendNotFound(exchange, "Path not found");
        }
    }

    private void handleSubtasksPath(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            String json = GSON.toJson(subtasks);
            sendSuccess(exchange, json);
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Subtask subtask = parseSubtaskFromJson(body);
            if (subtask == null) {
                sendBadRequest(exchange, "Invalid subtask data");
                return;
            }
            // Проверяем, существует ли epic
            if (taskManager.getEpic(subtask.getEpicId()) == null) {
                sendNotFound(exchange, "Epic not found");
                return;
            }
            int id = taskManager.addSubtask(subtask);
            //subtask.setId(id);
            sendCreated(exchange, GSON.toJson(subtask));
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    private void handleSubtaskByIdPath(HttpExchange exchange, String method) throws IOException {
        Optional<Integer> idOpt = getIdFromPathParameter(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid ID format");
            return;
        }
        int id = idOpt.get();

        if ("GET".equals(method)) {
            Subtask subtask = taskManager.getSubtask(id); // Предполагаем, что getSubtask возвращает Subtask или null
            if (subtask == null) {
                sendNotFound(exchange, "Subtask not found");
                return;
            }
            sendSuccess(exchange, GSON.toJson(subtask));
        } else if ("DELETE".equals(method)) {
            taskManager.removeSubtask(id); // Предполагаем, что removeSubtask - void
            sendSuccess(exchange, "Subtask deleted");
        } else if ("PUT".equals(method)) {
            String body = readRequestBody(exchange);
            Subtask updatedSubtask = parseSubtaskFromJson(body);
            if (updatedSubtask == null) {
                sendBadRequest(exchange, "Invalid subtask data");
                return;
            }
            updatedSubtask.setId(id);
            boolean updated = taskManager.updateSubtask(updatedSubtask);
            if (updated) {
                sendSuccess(exchange, GSON.toJson(updatedSubtask));
            } else {
                sendNotFound(exchange, "Subtask not found");
            }
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    private Subtask parseSubtaskFromJson(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            String title = json.get("title").getAsString();
            String description = json.has("description") ? json.get("description").getAsString() : "";
            String statusStr = json.has("status") ? json.get("status").getAsString() : "NEW";
            Status status = Status.valueOf(statusStr.toUpperCase());

            int epicId = json.get("epicId").getAsInt();

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

            LocalDateTime endTime = null;
            if (json.has("endTime")) {
                String endTimeStr = json.get("endTime").getAsString();
                endTime = LocalDateTime.parse(endTimeStr);
            }

            // Создаём Subtask с помощью конструкторов из класса Subtask
            Subtask subtask = new Subtask(title, description, epicId, startTime, duration);

            if (!status.equals(Status.NEW)) {
                subtask.setStatus(status);
            }

            if (endTime != null) {
                subtask.setEndTime(endTime);
            }

            return subtask;
        } catch (Exception e) {
            // Обработка ошибок парсинга
            System.out.println("Произошла ошибка парсинга подзадачи " + e.getMessage());
            return null;
        }
    }
}
