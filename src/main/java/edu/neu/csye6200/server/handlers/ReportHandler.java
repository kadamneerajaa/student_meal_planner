package edu.neu.csye6200.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.neu.csye6200.model.DailySummary;
import edu.neu.csye6200.model.DayPlan;
import edu.neu.csye6200.model.Student;
import edu.neu.csye6200.model.WeeklyPlan;
import edu.neu.csye6200.model.WeeklySummary;
import edu.neu.csye6200.service.PlanService;
import edu.neu.csye6200.service.ReportService;

public class ReportHandler implements HttpHandler {
    private ReportService reportService;
    private PlanService planService;
    
    public ReportHandler(ReportService reportService, PlanService planService) {
        this.reportService = reportService;
        this.planService = planService;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        
        System.out.println("[ReportHandler] " + method + " " + path + (query != null ? "?" + query : ""));
        
        try {
            if (method.equals("GET") && path.equals("/api/reports/daily")) {
                handleDailySummary(exchange, query);
            } else if (method.equals("GET") && path.equals("/api/reports/weekly")) {
                handleWeeklySummary(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    private void handleDailySummary(HttpExchange exchange, String query) throws IOException {
        if (query == null || !query.startsWith("day=")) {
            sendResponse(exchange, 400, "{\"error\":\"day parameter required\"}");
            return;
        }
        
        String dayName = java.net.URLDecoder.decode(query.substring(4), StandardCharsets.UTF_8);
        System.out.println("[ReportHandler] Fetching daily summary for: " + dayName);
        
        WeeklyPlan plan = planService.getWeeklyPlan();
        
        if (plan == null) {
            System.out.println("[ReportHandler] No weekly plan found");
            sendResponse(exchange, 404, "{\"error\":\"No weekly plan found\"}");
            return;
        }
        
        try {
            DayPlan dayPlan = planService.getDayPlan(dayName);
            Student student = plan.getStudent();
            
            System.out.println("[ReportHandler] DayPlan meals - Breakfast: " + (dayPlan.getBreakfast() != null) + 
                             ", Lunch: " + (dayPlan.getLunch() != null) + 
                             ", Dinner: " + (dayPlan.getDinner() != null));
            
            DailySummary summary = reportService.generateDailySummary(dayPlan, student);
            
            System.out.println("[ReportHandler] Daily summary - Calories: " + summary.getTotalCalories() + 
                             ", Cost: " + summary.getTotalCost());
            
            String json = dailySummaryToJson(summary);
            sendResponse(exchange, 200, json);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\":\"Day not found: " + escapeJson(dayName) + "\"}");
        }
    }
    
    private void handleWeeklySummary(HttpExchange exchange) throws IOException {
        System.out.println("[ReportHandler] Fetching weekly summary");
        
        WeeklyPlan plan = planService.getWeeklyPlan();
        
        if (plan == null) {
            System.out.println("[ReportHandler] No weekly plan found");
            sendResponse(exchange, 404, "{\"error\":\"No weekly plan found\"}");
            return;
        }
        
        Student student = plan.getStudent();
        WeeklySummary summary = reportService.generateWeeklySummary(plan, student);
        
        System.out.println("[ReportHandler] Weekly summary - Total Calories: " + summary.getWeeklyTotalCalories() + 
                         ", Total Cost: " + summary.getWeeklyTotalCost());
        
        String json = weeklySummaryToJson(summary, plan);
        sendResponse(exchange, 200, json);
    }
    
    private String dailySummaryToJson(DailySummary summary) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"dayPlan\":{\"dayName\":\"").append(escapeJson(summary.getDayPlan().getDayName())).append("\"},");
        json.append("\"totalCalories\":").append(summary.getTotalCalories()).append(",");
        json.append("\"totalProtein\":").append(summary.getTotalProtein()).append(",");
        json.append("\"totalCarbs\":").append(summary.getTotalCarbs()).append(",");
        json.append("\"totalFat\":").append(summary.getTotalFat()).append(",");
        json.append("\"totalCost\":").append(summary.getTotalCost()).append(",");
        json.append("\"calorieGoalMet\":").append(summary.isCalorieGoalMet()).append(",");
        json.append("\"macroGoalsMet\":").append(summary.isMacroGoalsMet());
        json.append("}");
        return json.toString();
    }
    
    private String weeklySummaryToJson(WeeklySummary summary, WeeklyPlan plan) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"weeklyTotalCalories\":").append(summary.getWeeklyTotalCalories()).append(",");
        json.append("\"weeklyTotalProtein\":").append(summary.getWeeklyTotalProtein()).append(",");
        json.append("\"weeklyTotalCarbs\":").append(summary.getWeeklyTotalCarbs()).append(",");
        json.append("\"weeklyTotalFat\":").append(summary.getWeeklyTotalFat()).append(",");
        json.append("\"weeklyTotalCost\":").append(summary.getWeeklyTotalCost()).append(",");
        json.append("\"budgetMet\":").append(summary.isBudgetMet()).append(",");
        json.append("\"weeklyGoalsMet\":").append(summary.isWeeklyGoalsMet()).append(",");
        
        // Add ingredient usage summary
        json.append("\"ingredientUsageSummary\":{");
        Map<String, Double> ingredients = summary.getIngredientUsageSummary();
        if (ingredients != null && !ingredients.isEmpty()) {
            int count = 0;
            for (Map.Entry<String, Double> entry : ingredients.entrySet()) {
                if (count > 0) json.append(",");
                json.append("\"").append(escapeJson(entry.getKey())).append("\":").append(entry.getValue());
                count++;
            }
        }
        json.append("},");
        
        // Add daily breakdown for charts
        json.append("\"days\":[");
        for (int i = 0; i < plan.getDays().size(); i++) {
            if (i > 0) json.append(",");
            DayPlan day = plan.getDays().get(i);
            json.append("{");
            json.append("\"dayName\":\"").append(escapeJson(day.getDayName())).append("\",");
            json.append("\"totalCalories\":").append(day.calculateDailyCalories()).append(",");
            json.append("\"totalProtein\":").append(day.calculateDailyProtein()).append(",");
            json.append("\"totalCarbs\":").append(day.calculateDailyCarbs()).append(",");
            json.append("\"totalFat\":").append(day.calculateDailyFat()).append(",");
            json.append("\"totalCost\":").append(day.calculateDailyCost());
            json.append("}");
        }
        json.append("]");
        
        json.append("}");
        return json.toString();
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