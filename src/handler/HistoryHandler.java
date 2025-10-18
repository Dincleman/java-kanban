package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/history")) {
            handleHistoryPath(exchange, method);
        } else {
            sendNotFound(exchange, "Path not found");
        }
    }

    private void handleHistoryPath(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            List<Task> history = taskManager.getHistory();
            String json = GSON.toJson(history);
            sendSuccess(exchange, json);
        } else {
            sendBadRequest(exchange, "Method not allowed");
        }
    }
}
