package edu.neu.csye6200.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.neu.csye6200.model.Student;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * StudentHandler - Handles HTTP requests for student profiles
 * API Endpoints:
 * - GET /api/student - Get current student profile
 * - PUT /api/student - Update student profile
 */
public class StudentHandler implements HttpHandler {
    
    // In-memory student (in real implementation, use a repository)
    private Student currentStudent;
    
    public StudentHandler() {
        // Initialize with a default student
        this.currentStudent = new Student(
            "S001",
            "John Doe",
            "Veg",
            2000.0,
            150.0,
            250.0,
            65.0,
            100.0
        );
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            if (method.equals("GET") && path.equals("/api/student")) {
                handleGetStudent(exchange);
            } else if (method.equals("PUT") && path.equals("/api/student")) {
                handleUpdateStudent(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * GET /api/student - Returns current student profile
     */
    private void handleGetStudent(HttpExchange exchange) throws IOException {
        String json = studentToJson(currentStudent);
        sendResponse(exchange, 200, json);
    }
    
    /**
     * PUT /api/student - Updates student profile
     * Expected JSON body:
     * {
     *   "id": "S001",
     *   "name": "John Doe",
     *   "dietaryPreference": "Veg",
     *   "dailyCalorieGoal": 2000,
     *   "proteinGoal": 150,
     *   "carbsGoal": 250,
     *   "fatsGoal": 65,
     *   "weeklyBudget": 100
     * }
     */
    private void handleUpdateStudent(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        
        try {
            // Parse JSON manually (or use a JSON library)
            String id = extractJsonValue(requestBody, "id");
            String name = extractJsonValue(requestBody, "name");
            String dietaryPreference = extractJsonValue(requestBody, "dietaryPreference");
            double dailyCalorieGoal = extractJsonDouble(requestBody, "dailyCalorieGoal");
            double proteinGoal = extractJsonDouble(requestBody, "proteinGoal");
            double carbsGoal = extractJsonDouble(requestBody, "carbsGoal");
            double fatsGoal = extractJsonDouble(requestBody, "fatsGoal");
            double weeklyBudget = extractJsonDouble(requestBody, "weeklyBudget");
            
            // Update student
            this.currentStudent = new Student(
                id != null ? id : currentStudent.getId(),
                name != null ? name : currentStudent.getName(),
                dietaryPreference != null ? dietaryPreference : currentStudent.getDietaryPreference(),
                dailyCalorieGoal > 0 ? dailyCalorieGoal : currentStudent.getDailyCalorieGoal(),
                proteinGoal > 0 ? proteinGoal : currentStudent.getProteinGoal(),
                carbsGoal > 0 ? carbsGoal : currentStudent.getCarbsGoal(),
                fatsGoal > 0 ? fatsGoal : currentStudent.getFatsGoal(),
                weeklyBudget > 0 ? weeklyBudget : currentStudent.getWeeklyBudget()
            );
            
            String json = studentToJson(currentStudent);
            sendResponse(exchange, 200, json);
            
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid student data: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Converts Student to JSON
     */
    private String studentToJson(Student student) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\": \"").append(student.getId()).append("\",");
        json.append("\"name\": \"").append(student.getName()).append("\",");
        json.append("\"dietaryPreference\": \"").append(student.getDietaryPreference()).append("\",");
        json.append("\"dailyCalorieGoal\": ").append(student.getDailyCalorieGoal()).append(",");
        json.append("\"proteinGoal\": ").append(student.getProteinGoal()).append(",");
        json.append("\"carbsGoal\": ").append(student.getCarbsGoal()).append(",");
        json.append("\"fatsGoal\": ").append(student.getFatsGoal()).append(",");
        json.append("\"weeklyBudget\": ").append(student.getWeeklyBudget());
        json.append("}");
        return json.toString();
    }
    
    /**
     * Reads the request body as a string
     */
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
    
    /**
     * Extracts a string value from simple JSON
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;
        
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;
        
        return json.substring(startQuote + 1, endQuote);
    }
    
    /**
     * Extracts a double value from simple JSON
     */
    private double extractJsonDouble(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return -1;
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return -1;
        
        int commaIndex = json.indexOf(",", colonIndex);
        int braceIndex = json.indexOf("}", colonIndex);
        int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;
        
        if (endIndex == -1) return -1;
        
        String valueStr = json.substring(colonIndex + 1, endIndex).trim();
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Sends an HTTP response
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    /**
     * Gets the current student (for use by other handlers)
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }
}