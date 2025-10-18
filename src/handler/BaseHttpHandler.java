package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import manager.TaskManager;
import ru.common.util.GsonFactory;

public abstract class BaseHttpHandler implements HttpHandler {

    protected static final Gson GSON = GsonFactory.createGson();
    protected final TaskManager taskManager;

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendSuccess(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, 200);
    }

    protected void sendCreated(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, 201);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 400);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendInteractions(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 406);
    }

    protected void sendInternalError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 500);
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected Optional<Integer> getIdFromPathParameter(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length < 3) {
            return Optional.empty();
        }
        String idFromUrl = pathParts[2];
        try {
            return Optional.of(Integer.parseInt(idFromUrl));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;
}