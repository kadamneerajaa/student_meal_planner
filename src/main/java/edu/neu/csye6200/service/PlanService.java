package edu.neu.csye6200.service;

import edu.neu.csye6200.data.PlanCsvRepository;
import edu.neu.csye6200.model.DayPlan;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.model.Student;
import edu.neu.csye6200.model.WeeklyPlan;

public class PlanService {
    private PlanCsvRepository planRepository;
    private WeeklyPlan currentPlan;
    
    public PlanService() {
        this.planRepository = new PlanCsvRepository();
        // Initialize with a default student plan
        initializeDefaultPlan();
    }
    
    private void initializeDefaultPlan() {
        // Create a default student if no plan exists
        Student defaultStudent = new Student(
            "S001", 
            "John Doe", 
            "Veg", 
            2000.0, 
            150.0, 
            250.0, 
            65.0, 
            100.0
        );
        this.currentPlan = new WeeklyPlan(defaultStudent);
    }
    
    public WeeklyPlan createWeeklyPlan(Student student) {
        this.currentPlan = new WeeklyPlan(student);
        return currentPlan;
    }
    
    public void assignMealToDay(String dayName, String mealSlot, Meal meal) {
        if (currentPlan == null) {
            initializeDefaultPlan();
        }
        
        DayPlan day = currentPlan.getDayPlan(dayName)
            .orElseThrow(() -> new IllegalArgumentException("Invalid day: " + dayName));
        
        switch (mealSlot.toLowerCase()) {
            case "breakfast":
                day.setBreakfast(meal);
                break;
            case "lunch":
                day.setLunch(meal);
                break;
            case "dinner":
                day.setDinner(meal);
                break;
            case "quick":
                day.getQuickMeals().add(meal);
                break;
            default:
                throw new IllegalArgumentException("Invalid meal slot: " + mealSlot);
        }
    }
    
    public WeeklyPlan getWeeklyPlan() {
        if (currentPlan == null) {
            initializeDefaultPlan();
        }
        return currentPlan;
    }
    
    public DayPlan getDayPlan(String dayName) {
        if (currentPlan == null) {
            initializeDefaultPlan();
        }
        return currentPlan.getDayPlan(dayName)
            .orElseThrow(() -> new IllegalArgumentException("Invalid day: " + dayName));
    }
    
    public void saveWeeklyPlan(WeeklyPlan plan) {
        this.currentPlan = plan;
        planRepository.saveWeeklyPlan(plan);
    }
    
    public WeeklyPlan loadWeeklyPlan(String studentId) {
        this.currentPlan = planRepository.loadWeeklyPlan(studentId);
        if (this.currentPlan == null) {
            initializeDefaultPlan();
        }
        return currentPlan;
    }
}