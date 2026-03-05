package edu.neu.csye6200.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Pantry;
import edu.neu.csye6200.model.ShoppingItem;
import edu.neu.csye6200.model.ShoppingList;
import edu.neu.csye6200.model.Student;
import edu.neu.csye6200.model.WeeklyPlan;
import edu.neu.csye6200.service.PlanService;
import edu.neu.csye6200.service.ShoppingService;

public class ShoppingHandler implements HttpHandler {

    private final ShoppingService shoppingService;
    private final PlanService planService;

    public ShoppingHandler(ShoppingService shoppingService, PlanService planService) {
        this.shoppingService = shoppingService;
        this.planService = planService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        try {
            if (path.equals("/api/shopping/list")) {
                handleShoppingList(exchange, method);
            } else if (path.equals("/api/shopping/pantry")) {
                handlePantryRoot(exchange, method);
            } else if (path.startsWith("/api/shopping/pantry/")) {
                handlePantryItem(exchange, method, path);
            } else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * GET /api/shopping/list
     * Generates a shopping list based on the current weekly plan and pantry,
     * calculates total cost and compares it to the student's weekly budget.
     */
    private void handleShoppingList(HttpExchange exchange, String method) throws IOException {
        if (!"GET".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        WeeklyPlan weeklyPlan = planService.getWeeklyPlan();
        if (weeklyPlan == null) {
            sendJsonResponse(exchange, 400, "{\"error\":\"No weekly plan available\"}");
            return;
        }

        Student student = weeklyPlan.getStudent();
        double weeklyBudget = (student != null) ? student.getWeeklyBudget() : 0.0;

        ShoppingList shoppingList = shoppingService.generateShoppingList(weeklyPlan);
        double totalCost = shoppingService.calculateTotalCost(shoppingList);
        boolean withinBudget = shoppingService.isWithinBudget(shoppingList, weeklyBudget);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"items\":[");

        int count = 0;
        for (ShoppingItem item : shoppingList.getItemsView()) {
            if (count > 0) {
                json.append(",");
            }
            json.append("{");
            json.append("\"name\":\"").append(escapeJson(item.getName())).append("\",");
            json.append("\"unit\":\"").append(escapeJson(item.getUnit() != null ? item.getUnit() : "")).append("\",");
            json.append("\"requiredQuantity\":").append(item.getRequiredQuantity()).append(",");
            json.append("\"costPerUnit\":").append(item.getCostPerUnit()).append(",");
            json.append("\"totalCost\":").append(item.getTotalCost());
            json.append("}");
            count++;
        }

        json.append("],");
        json.append("\"totalCost\":").append(totalCost).append(",");
        json.append("\"weeklyBudget\":").append(weeklyBudget).append(",");
        json.append("\"withinBudget\":").append(withinBudget);
        json.append("}");

        sendJsonResponse(exchange, 200, json.toString());
    }

    private void handlePantryRoot(HttpExchange exchange, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            Pantry pantry = shoppingService.getPantry();

            StringBuilder json = new StringBuilder("[");
            int count = 0;
            for (Ingredient ing : pantry.getIngredientsView()) {
                if (count > 0) json.append(",");
                json.append("{");
                json.append("\"id\":\"").append(escapeJson(ing.getName())).append("\",");
                json.append("\"name\":\"").append(escapeJson(ing.getName())).append("\",");
                json.append("\"unit\":\"").append(escapeJson(ing.getUnit() != null ? ing.getUnit() : "")).append("\",");
                json.append("\"quantity\":").append(ing.getQuantity()).append(",");
                json.append("\"costPerUnit\":").append(ing.getCostPerUnit());
                json.append("}");
                count++;
            }
            json.append("]");
            sendJsonResponse(exchange, 200, json.toString());

        } else if ("POST".equalsIgnoreCase(method)) {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getRawQuery());
                String name = params.get("name");
                String unit = params.get("unit");
                
                if (name == null || name.trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Item name is required\"}");
                    return;
                }
                
                if (unit == null || unit.trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Unit is required\"}");
                    return;
                }
                
                double quantity = parseDouble(params.get("quantity"));
                double costPerUnit = parseDouble(params.get("costPerUnit"));

                if (quantity <= 0) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Quantity must be greater than 0\"}");
                    return;
                }

                Ingredient ingredient = new Ingredient(name, unit, quantity, costPerUnit);
                shoppingService.addToPantry(ingredient);

                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Pantry item added\"}");
            } catch (IllegalArgumentException e) {
                sendJsonResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        } else {
            sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private void handlePantryItem(HttpExchange exchange, String method, String path) throws IOException {
        String name = path.substring("/api/shopping/pantry/".length());
        
        // Decode the URL-encoded name
        name = decode(name);

        if ("PUT".equalsIgnoreCase(method)) {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getRawQuery());
                String unit = params.get("unit");
                
                if (unit == null || unit.trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Unit is required\"}");
                    return;
                }
                
                double quantity = parseDouble(params.get("quantity"));
                double costPerUnit = parseDouble(params.get("costPerUnit"));

                if (quantity <= 0) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Quantity must be greater than 0\"}");
                    return;
                }

                Ingredient ingredient = new Ingredient(name, unit, quantity, costPerUnit);
                shoppingService.updatePantryItem(ingredient);

                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Pantry item updated\"}");
            } catch (IllegalArgumentException e) {
                sendJsonResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
            
        } else if ("DELETE".equalsIgnoreCase(method)) {
            try {
                shoppingService.removePantryItem(name);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Pantry item deleted\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
            
        } else {
            sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(decode(kv[0]), decode(kv[1]));
            }
        }
        return params;
    }

    private String decode(String value) {
        return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private double parseDouble(String value) {
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
