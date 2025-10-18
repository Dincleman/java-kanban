package handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/epics")) {
            handleEpicsPath(exchange, method);
        } else if (path.matches("/epics/\\d+")) {
            handleEpicByIdPath(exchange, method);
        } else {
            sendNotFound(exchange, "Path not found");
        }
    }

    private void handleEpicsPath(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            List<Epic> epics = taskManager.getAllEpics(); // Предполагаем, что TaskManager имеет метод getAllEpics(), возвращающий List<Epic>
            String json = GSON.toJson(epics);
            sendSuccess(exchange, json);
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Epic epic = parseEpicFromJson(body);
            if (epic == null) {
                sendBadRequest(exchange, "Invalid epic data");
                return;
            }
            int id = taskManager.addEpic(epic); // Предполагаем, что addEpic возвращает int (id новой задачи)
            epic.setId(id);
            sendCreated(exchange, GSON.toJson(epic));
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    private void handleEpicByIdPath(HttpExchange exchange, String method) throws IOException {
        Optional<Integer> idOpt = getIdFromPathParameter(exchange);
        if (idOpt.isEmpty()) {
            sendBadRequest(exchange, "Invalid ID format");
            return;
        }
        int id = idOpt.get();

        if ("GET".equals(method)) {
            Epic epic = taskManager.getEpic(id); // Предполагаем, что getEpic возвращает Epic или null
            if (epic == null) {
                sendNotFound(exchange, "Epic not found");
                return;
            }
            sendSuccess(exchange, GSON.toJson(epic));
        } else if ("DELETE".equals(method)) {
            taskManager.removeEpic(id); // Предполагаем, что removeEpic - void
            sendSuccess(exchange, "Epic deleted");
        } else if ("PUT".equals(method)) {
            String body = readRequestBody(exchange);
            Epic updatedEpic = parseEpicFromJson(body);
            if (updatedEpic == null) {
                sendBadRequest(exchange, "Invalid epic data");
                return;
            }
            updatedEpic.setId(id);
            boolean updated = taskManager.updateEpic(updatedEpic); // Предполагаем, что updateEpic возвращает boolean
            if (updated) {
                sendSuccess(exchange, GSON.toJson(updatedEpic));
            } else {
                sendNotFound(exchange, "Epic not found");
            }
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }

    private Epic parseEpicFromJson(String jsonString) {
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

            LocalDateTime endTime = null;
            if (json.has("endTime")) {
                String endTimeStr = json.get("endTime").getAsString();
                endTime = LocalDateTime.parse(endTimeStr);
            }

            // Создаём Epic с помощью конструкторов из класса Epic
            Epic epic = new Epic(title, description, startTime, duration);

            if (endTime != null) {
                epic.setEndTime(endTime);
            }
            if (!status.equals(Status.NEW)) {
                epic.setStatus(status);
            }

            return epic;
        } catch (Exception e) {
            // Обработка ошибок парсинга
            System.out.println("Произошла ошибка парсинга эпика " + e.getMessage());
            return null;
        }
    }
}
