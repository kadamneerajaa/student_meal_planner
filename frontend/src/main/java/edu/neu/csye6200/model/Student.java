package edu.neu.csye6200.model;

public class Student {
    private String id;
    private String name;
    private String dietaryPreference; // Veg, Vegan, Meat Eater, Gluten-Free
    private double dailyCalorieGoal;
    private double proteinGoal;
    private double carbsGoal;
    private double fatsGoal;
    private double weeklyBudget;
    
    // Constructor
    public Student(String id, String name, String dietaryPreference, 
                   double dailyCalorieGoal, double proteinGoal, 
                   double carbsGoal, double fatsGoal, double weeklyBudget) {
        this.id = id;
        this.name = name;
        this.dietaryPreference = dietaryPreference;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.proteinGoal = proteinGoal;
        this.carbsGoal = carbsGoal;
        this.fatsGoal = fatsGoal;
        this.weeklyBudget = weeklyBudget;
    }
    
    // Default constructor
    public Student() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDietaryPreference() {
        return dietaryPreference;
    }
    
    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }
    
    public double getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }
    
    public void setDailyCalorieGoal(double dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
    
    public double getProteinGoal() {
        return proteinGoal;
    }
    
    public void setProteinGoal(double proteinGoal) {
        this.proteinGoal = proteinGoal;
    }
    
    public double getCarbsGoal() {
        return carbsGoal;
    }
    
    public void setCarbsGoal(double carbsGoal) {
        this.carbsGoal = carbsGoal;
    }
    
    public double getFatsGoal() {
        return fatsGoal;
    }
    
    public void setFatsGoal(double fatsGoal) {
        this.fatsGoal = fatsGoal;
    }
    
    public double getWeeklyBudget() {
        return weeklyBudget;
    }
    
    public void setWeeklyBudget(double weeklyBudget) {
        this.weeklyBudget = weeklyBudget;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dietaryPreference='" + dietaryPreference + '\'' +
                ", dailyCalorieGoal=" + dailyCalorieGoal +
                ", proteinGoal=" + proteinGoal +
                ", carbsGoal=" + carbsGoal +
                ", fatsGoal=" + fatsGoal +
                ", weeklyBudget=" + weeklyBudget +
                '}';
    }
}