package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PriorityHandler extends BaseHttpHandler {
    public PriorityHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String prioritizedJson = GSON.toJson(taskManager.getPrioritizedTasks());
            sendSuccess(exchange, prioritizedJson);
        } else {
            sendBadRequest(exchange, "Метод не поддерживается");
        }
    }
}