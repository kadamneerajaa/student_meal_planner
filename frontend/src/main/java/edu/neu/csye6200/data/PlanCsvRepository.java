package edu.neu.csye6200.data;

import edu.neu.csye6200.model.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for managing Weekly Plans in CSV format
 * Hardcoded implementation for frontend-backend integration
 */
public class PlanCsvRepository {
    private static final String FILE_PATH = "data/weekly_plans.csv";
    private static final String HEADER = "studentId,dayName,breakfastId,lunchId,dinnerId,quickMealIds";
    
    // Hardcoded meal database
    private static final Map<String, Meal> MEAL_DATABASE = new HashMap<>();
    
    static {
        // Breakfast meals
        MEAL_DATABASE.put("B-001", createMeal("B-001", "Oatmeal with Berries", "BREAKFAST", 350, 15, 55, 8, 4.50, false));
        MEAL_DATABASE.put("B-002", createMeal("B-002", "Scrambled Eggs & Toast", "BREAKFAST", 400, 25, 40, 18, 5.00, false));
        MEAL_DATABASE.put("B-003", createMeal("B-003", "Greek Yogurt Parfait", "BREAKFAST", 300, 20, 45, 5, 4.00, false));
        MEAL_DATABASE.put("B-004", createMeal("B-004", "Pancakes with Syrup", "BREAKFAST", 450, 12, 75, 10, 5.50, false));
        MEAL_DATABASE.put("B-005", createMeal("B-005", "Avocado Toast", "BREAKFAST", 380, 12, 42, 20, 6.00, false));
        
        // Lunch meals
        MEAL_DATABASE.put("L-001", createMeal("L-001", "Grilled Chicken Salad", "LUNCH", 450, 35, 30, 18, 8.50, false));
        MEAL_DATABASE.put("L-002", createMeal("L-002", "Veggie Wrap", "LUNCH", 400, 15, 60, 12, 7.00, false));
        MEAL_DATABASE.put("L-003", createMeal("L-003", "Quinoa Bowl", "LUNCH", 500, 18, 70, 15, 9.00, false));
        MEAL_DATABASE.put("L-004", createMeal("L-004", "Turkey Sandwich", "LUNCH", 420, 28, 50, 12, 7.50, false));
        MEAL_DATABASE.put("L-005", createMeal("L-005", "Pasta Primavera", "LUNCH", 550, 20, 80, 15, 8.00, false));
        
        // Dinner meals
        MEAL_DATABASE.put("D-001", createMeal("D-001", "Salmon with Rice", "DINNER", 650, 40, 60, 25, 12.00, false));
        MEAL_DATABASE.put("D-002", createMeal("D-002", "Chicken Stir Fry", "DINNER", 600, 45, 55, 20, 10.50, false));
        MEAL_DATABASE.put("D-003", createMeal("D-003", "Veggie Curry", "DINNER", 500, 15, 70, 18, 9.00, false));
        MEAL_DATABASE.put("D-004", createMeal("D-004", "Beef Tacos", "DINNER", 700, 35, 65, 30, 11.00, false));
        MEAL_DATABASE.put("D-005", createMeal("D-005", "Tofu Stir Fry", "DINNER", 480, 25, 50, 20, 8.50, false));
        
        // Quick meals
        MEAL_DATABASE.put("Q-001", createMeal("Q-001", "Protein Shake", "SNACK", 200, 25, 15, 5, 3.50, true));
        MEAL_DATABASE.put("Q-002", createMeal("Q-002", "Apple & Peanut Butter", "SNACK", 250, 8, 30, 12, 2.50, true));
        MEAL_DATABASE.put("Q-003", createMeal("Q-003", "Trail Mix", "SNACK", 300, 10, 35, 18, 3.00, true));
        MEAL_DATABASE.put("Q-004", createMeal("Q-004", "Granola Bar", "SNACK", 180, 5, 28, 6, 2.00, true));
        MEAL_DATABASE.put("Q-005", createMeal("Q-005", "Hummus & Veggies", "SNACK", 150, 6, 20, 7, 3.50, true));
    }
    
    /**
     * Helper method to create a meal
     */
    private static Meal createMeal(String id, String name, String mealType,
                                   int calories, double protein, double carbs,
                                   double fat, double cost, boolean isQuick) {
        return new HardcodedMeal(id, name, mealType, calories, protein, carbs, fat, cost, isQuick);
    }
    
    public PlanCsvRepository() {
        ensureDataDirectoryExists();
        ensureFileExists();
    }
    
    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("[PlanCsvRepository] Created data directory");
        }
    }
    
    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
                writer.println(HEADER);
                System.out.println("[PlanCsvRepository] Created weekly_plans.csv with header");
            } catch (IOException e) {
                System.err.println("[PlanCsvRepository] Error creating weekly_plans.csv: " + e.getMessage());
            }
        }
    }
    
    public void saveWeeklyPlan(WeeklyPlan plan) {
        if (plan == null || plan.getStudent() == null) {
            System.err.println("[PlanCsvRepository] Cannot save null plan or plan with null student");
            return;
        }
        
        clearStudentPlan(plan.getStudent().getId());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            for (DayPlan day : plan.getDays()) {
                StringBuilder line = new StringBuilder();
                line.append(plan.getStudent().getId()).append(",");
                line.append(day.getDayName()).append(",");
                line.append(getMealId(day.getBreakfast())).append(",");
                line.append(getMealId(day.getLunch())).append(",");
                line.append(getMealId(day.getDinner())).append(",");
                
                String quickMealIds = day.getQuickMeals().stream()
                    .map(this::getMealId)
                    .filter(id -> !id.isEmpty())
                    .collect(Collectors.joining("|"));
                line.append(quickMealIds);
                
                writer.println(line.toString());
            }
            System.out.println("[PlanCsvRepository] Saved weekly plan for student: " + plan.getStudent().getId());
        } catch (IOException e) {
            System.err.println("[PlanCsvRepository] Error saving weekly plan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public WeeklyPlan loadWeeklyPlan(String studentId) {
        Map<String, DayPlan> dayPlansMap = new HashMap<>();
        Student student = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    continue;
                }
                
                String fileStudentId = parts[0].trim();
                
                if (!fileStudentId.equals(studentId)) {
                    continue;
                }
                
                if (student == null) {
                    student = getHardcodedStudent(studentId);
                }
                
                String dayName = parts[1].trim();
                String breakfastId = parts[2].trim();
                String lunchId = parts[3].trim();
                String dinnerId = parts[4].trim();
                String quickMealIds = parts.length > 5 ? parts[5].trim() : "";
                
                DayPlan dayPlan = new DayPlan(dayName);
                
                if (!breakfastId.isEmpty()) {
                    dayPlan.setBreakfast(getMealById(breakfastId));
                }
                if (!lunchId.isEmpty()) {
                    dayPlan.setLunch(getMealById(lunchId));
                }
                if (!dinnerId.isEmpty()) {
                    dayPlan.setDinner(getMealById(dinnerId));
                }
                
                if (!quickMealIds.isEmpty()) {
                    String[] quickIds = quickMealIds.split("\\|");
                    for (String quickId : quickIds) {
                        if (!quickId.trim().isEmpty()) {
                            Meal quickMeal = getMealById(quickId.trim());
                            if (quickMeal != null) {
                                dayPlan.getQuickMeals().add(quickMeal);
                            }
                        }
                    }
                }
                
                dayPlansMap.put(dayName, dayPlan);
            }
            
            if (student == null) {
                System.out.println("[PlanCsvRepository] No plan found for student: " + studentId);
                return null;
            }
            
            WeeklyPlan weeklyPlan = new WeeklyPlan(student);
            
            for (int i = 0; i < weeklyPlan.getDays().size(); i++) {
                DayPlan day = weeklyPlan.getDays().get(i);
                if (dayPlansMap.containsKey(day.getDayName())) {
                    weeklyPlan.getDays().set(i, dayPlansMap.get(day.getDayName()));
                }
            }
            
            System.out.println("[PlanCsvRepository] Loaded weekly plan for student: " + studentId);
            return weeklyPlan;
            
        } catch (FileNotFoundException e) {
            System.err.println("[PlanCsvRepository] Weekly plans file not found: " + FILE_PATH);
            return null;
        } catch (IOException e) {
            System.err.println("[PlanCsvRepository] Error reading weekly plan: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void clearStudentPlan(String studentId) {
        List<String> linesToKeep = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    linesToKeep.add(line);
                    isHeader = false;
                    continue;
                }
                
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && !parts[0].trim().equals(studentId)) {
                    linesToKeep.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("[PlanCsvRepository] Error reading file during clear: " + e.getMessage());
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String line : linesToKeep) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("[PlanCsvRepository] Error writing file during clear: " + e.getMessage());
        }
    }
    
    private String getMealId(Meal meal) {
        return meal != null ? meal.getId() : "";
    }
    
    private Student getHardcodedStudent(String studentId) {
        switch (studentId) {
            case "S001":
                return new Student("S001", "John Doe", "Veg", 2000.0, 150.0, 250.0, 65.0, 100.0);
            case "S002":
                return new Student("S002", "Jane Smith", "Vegan", 1800.0, 120.0, 220.0, 55.0, 90.0);
            case "S003":
                return new Student("S003", "Mike Johnson", "Meat Eater", 2200.0, 180.0, 270.0, 75.0, 120.0);
            default:
                return new Student(studentId, "Student " + studentId, "Veg", 2000.0, 150.0, 250.0, 65.0, 100.0);
        }
    }
    
    public Meal getMealById(String mealId) {
        return MEAL_DATABASE.get(mealId);
    }
    
    public static Set<String> getAllMealIds() {
        return MEAL_DATABASE.keySet();
    }
    
    public static Map<String, Meal> getAllMeals() {
        return new HashMap<>(MEAL_DATABASE);
    }
}

/**
 * Internal concrete Meal implementation for hardcoded data
 */
class HardcodedMeal extends Meal {
    
    private boolean isQuick;
    
    public HardcodedMeal(String id, String name, String mealType, 
                        int calories, double protein, double carbs, 
                        double fat, double cost, boolean isQuick) {
        super(id, name, mealType, "NONE", calories, protein, carbs, 
              fat, cost, new ArrayList<>(), false);
        this.isQuick = isQuick;
    }
    
    @Override
    public boolean isQuickMeal() {
        return isQuick;
    }
}