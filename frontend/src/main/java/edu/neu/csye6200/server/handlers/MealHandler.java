package edu.neu.csye6200.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.neu.csye6200.factory.MealFactory;
import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.service.MealService;

public class MealHandler implements HttpHandler {

    private static final String BASE_PATH = "/api/meals";
    private final MealService mealService;
    private final Gson gson;

    public MealHandler(MealService mealService) {
        this.mealService = mealService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            System.out.println("[MealHandler] " + method + " " + path);

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendText(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, 500, "Internal Server Error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

        if (path.equals(BASE_PATH + "/favorites")) {
            sendJson(exchange, 200, mealService.getFavoriteMeals());
            return;
        }

        if (path.equals(BASE_PATH + "/quick")) {
            int prepTime = Integer.parseInt(queryParams.getOrDefault("prepTime", "30"));
            sendJson(exchange, 200, mealService.getQuickMeals(prepTime));
            return;
        }

        if (path.equals(BASE_PATH + "/search")) {
            String name = queryParams.getOrDefault("name", "");
            sendJson(exchange, 200, mealService.searchMealsByName(name));
            return;
        }

        if (path.equals(BASE_PATH)) {
            String sort = queryParams.get("sort");
            List<Meal> result = (sort != null) 
                ? mealService.sortMeals(sort) 
                : mealService.getAllMeals();
            sendJson(exchange, 200, result);
            return;
        }

        if (path.startsWith(BASE_PATH + "/")) {
            String id = path.substring((BASE_PATH + "/").length());
            Meal meal = mealService.getMealById(id);
            if (meal == null) {
                sendText(exchange, 404, "Meal not found");
            } else {
                sendJson(exchange, 200, meal);
            }
            return;
        }

        sendText(exchange, 404, "Not Found");
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (!path.equals(BASE_PATH)) {
            sendText(exchange, 404, "Not Found");
            return;
        }

        try {
            String body = readBody(exchange);
            System.out.println("============================================");
            System.out.println("[MealHandler] Received POST request");
            System.out.println("[MealHandler] Body: " + body);
            
            MealDto dto = gson.fromJson(body, MealDto.class);
            System.out.println("[MealHandler] Parsed DTO: name=" + dto.name + ", mealType=" + dto.mealType);
            
            Meal meal = dtoToMeal(dto);
            System.out.println("[MealHandler] Created Meal object: " + meal);
            
            Meal created = mealService.createMeal(meal);
            System.out.println("[MealHandler] Saved meal with ID: " + created.getId());
            System.out.println("============================================");
            
            sendJson(exchange, 201, created);
        } catch (Exception e) {
            System.err.println("[MealHandler] ERROR creating meal:");
            e.printStackTrace();
            sendText(exchange, 500, "Failed to create meal: " + e.getMessage());
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith(BASE_PATH + "/")) {
            sendText(exchange, 404, "Not Found");
            return;
        }

        try {
            String id = path.substring((BASE_PATH + "/").length());
            String body = readBody(exchange);
            
            System.out.println("[MealHandler] PUT for ID: " + id);
            System.out.println("[MealHandler] PUT body: " + body);
            
            MealDto dto = gson.fromJson(body, MealDto.class);
            dto.id = id;
            
            Meal updated = mealService.updateMeal(dtoToMeal(dto));
            sendJson(exchange, 200, updated);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, 500, "Failed to update meal: " + e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith(BASE_PATH + "/")) {
            sendText(exchange, 404, "Not Found");
            return;
        }
        
        String id = path.substring((BASE_PATH + "/").length());
        boolean removed = mealService.deleteMeal(id);

        if (removed) {
            sendText(exchange, 204, "");
        } else {
            sendText(exchange, 404, "Meal not found");
        }
    }

    private static class MealDto {
        String id;
        String name;
        String mealType;
        String dietaryPreference;
        int calories;
        double protein;
        double carbs;
        double fat;
        double cost;
        String ingredients;
        boolean favoriteFlag;
        Integer prepTimeCategory;

        public void setId(String id) {
            this.id = id;
        }
    }

    private Meal dtoToMeal(MealDto dto) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (dto.ingredients != null && !dto.ingredients.isEmpty()) {
            String[] ingredientNames = dto.ingredients.split("\\|");
            for (String name : ingredientNames) {
                if (!name.trim().isEmpty()) {
                    ingredients.add(new Ingredient(name.trim(), "", 0, 0));
                }
            }
        }
        
        String type = (dto.prepTimeCategory == null || dto.prepTimeCategory <= 0)
                ? "REGULAR" : "QUICK";
        
        return MealFactory.createMeal(
                type,
                dto.id,
                dto.name != null ? dto.name : "",
                dto.mealType != null ? dto.mealType : "BREAKFAST",
                dto.dietaryPreference != null ? dto.dietaryPreference : "",
                dto.calories,
                dto.protein,
                dto.carbs,
                dto.fat,
                dto.cost,
                ingredients,
                dto.favoriteFlag,
                dto.prepTimeCategory
        );
    }

    private String readBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = urlDecode(kv[0]);
            String value = kv.length > 1 ? urlDecode(kv[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    private String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    // ONLY ONE sendJson method - with ingredients support
    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        // Convert meals to include ingredients
        Object convertedBody = body;
        if (body instanceof List) {
            List<?> list = (List<?>) body;
            if (!list.isEmpty() && list.get(0) instanceof Meal) {
                convertedBody = convertMealList((List<Meal>) list);
            }
        } else if (body instanceof Meal) {
            convertedBody = convertMeal((Meal) body);
        }
        
        String json = gson.toJson(convertedBody);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private List<Map<String, Object>> convertMealList(List<Meal> meals) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Meal meal : meals) {
            result.add(convertMeal(meal));
        }
        return result;
    }

    private Map<String, Object> convertMeal(Meal meal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", meal.getId());
        map.put("name", meal.getName());
        map.put("mealType", meal.getMealType());
        map.put("dietaryPreference", meal.getDietaryPreference());
        map.put("calories", meal.getCalories());
        map.put("protein", meal.getProtein());
        map.put("carbs", meal.getCarbs());
        map.put("fat", meal.getFat());
        map.put("cost", meal.getCost());
        map.put("favoriteFlag", meal.isFavoriteFlag());
        map.put("prepTimeCategory", meal.getPrepTimeCategory());
        
        // Add ingredients
        List<Map<String, Object>> ingredients = new ArrayList<>();
        if (meal.getIngredients() != null) {
            for (Ingredient ing : meal.getIngredients()) {
                Map<String, Object> ingMap = new HashMap<>();
                ingMap.put("name", ing.getName());
                ingMap.put("unit", ing.getUnit());
                ingMap.put("quantity", ing.getQuantity());
                ingMap.put("costPerUnit", ing.getCostPerUnit());
                ingredients.add(ingMap);
            }
        }
        map.put("ingredients", ingredients);
        
        return map;
    }

    private void sendText(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}