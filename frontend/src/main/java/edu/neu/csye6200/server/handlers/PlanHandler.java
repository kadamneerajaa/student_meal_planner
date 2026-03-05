package edu.neu.csye6200.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.neu.csye6200.model.DayPlan;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.model.Student;
import edu.neu.csye6200.model.WeeklyPlan;
import edu.neu.csye6200.service.MealService;
import edu.neu.csye6200.service.PlanService;

public class PlanHandler implements HttpHandler {
    private PlanService planService;
    private MealService mealService;
    private Gson gson;
    
    public PlanHandler(PlanService planService, MealService mealService) {
        this.planService = planService;
        this.mealService = mealService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        System.out.println("[PlanHandler] " + method + " " + path);
        
        try {
            if (method.equals("GET") && path.equals("/api/plans")) {
                handleGetWeeklyPlan(exchange);
            } else if (method.equals("POST") && path.equals("/api/plans")) {
                handleCreateOrUpdateWeeklyPlan(exchange);
            } else if (method.equals("GET") && path.matches("/api/plans/day/.*")) {
                handleGetDayPlan(exchange);
            } else if (method.equals("PUT") && path.matches("/api/plans/day/.*")) {
                handleUpdateDayPlan(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    private void handleGetWeeklyPlan(HttpExchange exchange) throws IOException {
        try {
            WeeklyPlan plan = planService.getWeeklyPlan();
            
            if (plan == null) {
                sendResponse(exchange, 404, "{\"error\":\"No weekly plan found\"}");
                return;
            }
            
            String json = gson.toJson(convertToDto(plan));
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Failed to get weekly plan: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    private void handleCreateOrUpdateWeeklyPlan(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            System.out.println("[PlanHandler] 📥 Received plan data: " + requestBody);
            
            WeeklyPlanDto dto = gson.fromJson(requestBody, WeeklyPlanDto.class);
            System.out.println("[PlanHandler] 📋 DTO has " + (dto.days != null ? dto.days.size() : 0) + " days");
            
            WeeklyPlan plan = convertFromDto(dto);
            System.out.println("[PlanHandler] 🔄 Converted plan has " + plan.getDays().size() + " days");
            
            // Log first day to verify meals
            if (!plan.getDays().isEmpty()) {
                DayPlan firstDay = plan.getDays().get(0);
                System.out.println("[PlanHandler] 🍽️ First day (" + firstDay.getDayName() + ") - Breakfast: " + 
                                 (firstDay.getBreakfast() != null ? firstDay.getBreakfast().getName() : "null"));
            }
            
            planService.saveWeeklyPlan(plan);
            String json = gson.toJson(convertToDto(plan));
            System.out.println("[PlanHandler] 📤 Sending response with " + plan.getDays().size() + " days");
            
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Failed to save plan: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    private void handleGetDayPlan(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String dayName = path.substring(path.lastIndexOf('/') + 1);
            
            System.out.println("[PlanHandler] Getting day plan for: " + dayName);
            
            DayPlan dayPlan = planService.getDayPlan(dayName);
            String json = gson.toJson(convertToDto(dayPlan));
            sendResponse(exchange, 200, json);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"Day not found: " + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Failed to get day plan: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    private void handleUpdateDayPlan(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String dayName = path.substring(path.lastIndexOf('/') + 1);
            String requestBody = readRequestBody(exchange);
            
            System.out.println("[PlanHandler] Updating day plan for: " + dayName);
            System.out.println("[PlanHandler] Request body: " + requestBody);
            
            DayPlanDto dto = gson.fromJson(requestBody, DayPlanDto.class);
            
            WeeklyPlan weeklyPlan = planService.getWeeklyPlan();
            
            DayPlan dayPlan = weeklyPlan.getDays().stream()
                .filter(d -> d.getDayName().equalsIgnoreCase(dayName))
                .findFirst()
                .orElse(new DayPlan(dayName));
            
            if (dto.breakfast != null && dto.breakfast.id != null && !dto.breakfast.id.isEmpty()) {
                Meal breakfast = mealService.getMealById(dto.breakfast.id);
                if (breakfast != null) dayPlan.setBreakfast(breakfast);
            } else {
                dayPlan.setBreakfast(null);
            }
            
            if (dto.lunch != null && dto.lunch.id != null && !dto.lunch.id.isEmpty()) {
                Meal lunch = mealService.getMealById(dto.lunch.id);
                if (lunch != null) dayPlan.setLunch(lunch);
            } else {
                dayPlan.setLunch(null);
            }
            
            if (dto.dinner != null && dto.dinner.id != null && !dto.dinner.id.isEmpty()) {
                Meal dinner = mealService.getMealById(dto.dinner.id);
                if (dinner != null) dayPlan.setDinner(dinner);
            } else {
                dayPlan.setDinner(null);
            }
            
            dayPlan.getQuickMeals().clear();
            if (dto.quickMeals != null) {
                for (MealDto mealDto : dto.quickMeals) {
                    if (mealDto != null && mealDto.id != null && !mealDto.id.isEmpty()) {
                        Meal meal = mealService.getMealById(mealDto.id);
                        if (meal != null) {
                            dayPlan.getQuickMeals().add(meal);
                        }
                    }
                }
            }
            
            planService.saveWeeklyPlan(weeklyPlan);
            
            String json = gson.toJson(convertToDto(dayPlan));
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Failed to update day plan: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    // DTO classes
    private static class WeeklyPlanDto {
        String studentId;
        String studentName;
        List<DayPlanDto> days;
    }
    
    private static class DayPlanDto {
        String dayName;
        MealDto breakfast;
        MealDto lunch;
        MealDto dinner;
        List<MealDto> quickMeals;
    }
    
    private static class MealDto {
        String id;
        String name;
        String mealType;
        int calories;
        double protein;
        double carbs;
        double fat;
        double cost;
    }
    
    // Conversion methods
    private WeeklyPlanDto convertToDto(WeeklyPlan plan) {
        WeeklyPlanDto dto = new WeeklyPlanDto();
        dto.studentId = plan.getStudent().getId();
        dto.studentName = plan.getStudent().getName();
        dto.days = new ArrayList<>();
        
        for (DayPlan day : plan.getDays()) {
            dto.days.add(convertToDto(day));
        }
        
        return dto;
    }
    
    private DayPlanDto convertToDto(DayPlan day) {
        DayPlanDto dto = new DayPlanDto();
        dto.dayName = day.getDayName();
        dto.breakfast = convertToDto(day.getBreakfast());
        dto.lunch = convertToDto(day.getLunch());
        dto.dinner = convertToDto(day.getDinner());
        dto.quickMeals = new ArrayList<>();
        
        for (Meal meal : day.getQuickMeals()) {
            dto.quickMeals.add(convertToDto(meal));
        }
        
        return dto;
    }
    
    private MealDto convertToDto(Meal meal) {
        if (meal == null) return null;
        
        MealDto dto = new MealDto();
        dto.id = meal.getId();
        dto.name = meal.getName();
        dto.mealType = meal.getMealType();
        dto.calories = meal.getCalories();
        dto.protein = meal.getProtein();
        dto.carbs = meal.getCarbs();
        dto.fat = meal.getFat();
        dto.cost = meal.getCost();
        
        return dto;
    }
    
    private WeeklyPlan convertFromDto(WeeklyPlanDto dto) {
        Student student = new Student(
            dto.studentId != null ? dto.studentId : "S001",
            dto.studentName != null ? dto.studentName : "Student",
            "Veg", 2000.0, 150.0, 250.0, 65.0, 100.0
        );
        
        WeeklyPlan plan = new WeeklyPlan(student);
        
        // CRITICAL: Clear the auto-initialized days first
        plan.getDays().clear();
        
        if (dto.days != null) {
            System.out.println("[PlanHandler] Converting " + dto.days.size() + " days from DTO");
            for (DayPlanDto dayDto : dto.days) {
                DayPlan day = convertFromDto(dayDto);
                plan.getDays().add(day);
            }
        }
        
        return plan;
    }
    
    private DayPlan convertFromDto(DayPlanDto dto) {
        DayPlan day = new DayPlan(dto.dayName);
        
        // Convert meals from DTOs to actual Meal objects
        if (dto.breakfast != null && dto.breakfast.id != null && !dto.breakfast.id.isEmpty()) {
            Meal breakfast = mealService.getMealById(dto.breakfast.id);
            if (breakfast != null) {
                day.setBreakfast(breakfast);
                System.out.println("[PlanHandler] Set breakfast for " + dto.dayName + ": " + breakfast.getName());
            }
        }
        
        if (dto.lunch != null && dto.lunch.id != null && !dto.lunch.id.isEmpty()) {
            Meal lunch = mealService.getMealById(dto.lunch.id);
            if (lunch != null) {
                day.setLunch(lunch);
                System.out.println("[PlanHandler] Set lunch for " + dto.dayName + ": " + lunch.getName());
            }
        }
        
        if (dto.dinner != null && dto.dinner.id != null && !dto.dinner.id.isEmpty()) {
            Meal dinner = mealService.getMealById(dto.dinner.id);
            if (dinner != null) {
                day.setDinner(dinner);
                System.out.println("[PlanHandler] Set dinner for " + dto.dayName + ": " + dinner.getName());
            }
        }
        
        // Handle quick meals
        if (dto.quickMeals != null) {
            for (MealDto mealDto : dto.quickMeals) {
                if (mealDto != null && mealDto.id != null && !mealDto.id.isEmpty()) {
                    Meal quickMeal = mealService.getMealById(mealDto.id);
                    if (quickMeal != null) {
                        day.getQuickMeals().add(quickMeal);
                    }
                }
            }
        }
        
        return day;
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
        );
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}