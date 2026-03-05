package edu.neu.csye6200.model;

import java.util.HashMap;
import java.util.Map;

public class WeeklySummary {
    private WeeklyPlan weeklyPlan;
    private double weeklyTotalCalories;
    private double weeklyTotalProtein;
    private double weeklyTotalCarbs;
    private double weeklyTotalFat;
    private double weeklyTotalCost;
    private boolean budgetMet;
    private boolean weeklyGoalsMet;
    private Map<String, Double> ingredientUsageSummary; // ingredient name -> total quantity
    
    public WeeklySummary(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
        this.ingredientUsageSummary = new HashMap<>();
        calculateWeeklyTotals();
        checkWeeklyGoals();
    }
    
    private void calculateWeeklyTotals() {
        // Use Java Streams to sum across all days
        this.weeklyTotalCalories = weeklyPlan.getDays().stream()
            .mapToDouble(DayPlan::calculateDailyCalories)
            .sum();
        
        this.weeklyTotalProtein = weeklyPlan.getDays().stream()
            .mapToDouble(DayPlan::calculateDailyProtein)
            .sum();
        
        this.weeklyTotalCarbs = weeklyPlan.getDays().stream()
            .mapToDouble(DayPlan::calculateDailyCarbs)
            .sum();
        
        this.weeklyTotalFat = weeklyPlan.getDays().stream()
            .mapToDouble(DayPlan::calculateDailyFat)
            .sum();
        
        this.weeklyTotalCost = weeklyPlan.getDays().stream()
            .mapToDouble(DayPlan::calculateDailyCost)
            .sum();
    }
    
    private void checkWeeklyGoals() {
        Student student = weeklyPlan.getStudent();
        double weeklyCalorieGoal = student.getDailyCalorieGoal() * 7;
        double weeklyProteinGoal = student.getProteinGoal() * 7;
        double weeklyCarbsGoal = student.getCarbsGoal() * 7;
        double weeklyFatGoal = student.getFatsGoal() * 7;
        
        boolean caloriesMatch = Math.abs(weeklyTotalCalories - weeklyCalorieGoal) 
                               < (weeklyCalorieGoal * 0.1);
        boolean macrosMatch = 
            Math.abs(weeklyTotalProtein - weeklyProteinGoal) < (weeklyProteinGoal * 0.1) &&
            Math.abs(weeklyTotalCarbs - weeklyCarbsGoal) < (weeklyCarbsGoal * 0.1) &&
            Math.abs(weeklyTotalFat - weeklyFatGoal) < (weeklyFatGoal * 0.1);
        
        this.weeklyGoalsMet = caloriesMatch && macrosMatch;
        this.budgetMet = weeklyTotalCost <= student.getWeeklyBudget();
    }
    
    // Getters and Setters
    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }
    
    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }
    
    public double getWeeklyTotalCalories() {
        return weeklyTotalCalories;
    }
    
    public void setWeeklyTotalCalories(double weeklyTotalCalories) {
        this.weeklyTotalCalories = weeklyTotalCalories;
    }
    
    public double getWeeklyTotalProtein() {
        return weeklyTotalProtein;
    }
    
    public void setWeeklyTotalProtein(double weeklyTotalProtein) {
        this.weeklyTotalProtein = weeklyTotalProtein;
    }
    
    public double getWeeklyTotalCarbs() {
        return weeklyTotalCarbs;
    }
    
    public void setWeeklyTotalCarbs(double weeklyTotalCarbs) {
        this.weeklyTotalCarbs = weeklyTotalCarbs;
    }
    
    public double getWeeklyTotalFat() {
        return weeklyTotalFat;
    }
    
    public void setWeeklyTotalFat(double weeklyTotalFat) {
        this.weeklyTotalFat = weeklyTotalFat;
    }
    
    public double getWeeklyTotalCost() {
        return weeklyTotalCost;
    }
    
    public void setWeeklyTotalCost(double weeklyTotalCost) {
        this.weeklyTotalCost = weeklyTotalCost;
    }
    
    public boolean isBudgetMet() {
        return budgetMet;
    }
    
    public void setBudgetMet(boolean budgetMet) {
        this.budgetMet = budgetMet;
    }
    
    public boolean isWeeklyGoalsMet() {
        return weeklyGoalsMet;
    }
    
    public void setWeeklyGoalsMet(boolean weeklyGoalsMet) {
        this.weeklyGoalsMet = weeklyGoalsMet;
    }
    
    public Map<String, Double> getIngredientUsageSummary() {
        return ingredientUsageSummary;
    }
    
    public void setIngredientUsageSummary(Map<String, Double> ingredientUsageSummary) {
        this.ingredientUsageSummary = ingredientUsageSummary;
    }
    
    @Override
    public String toString() {
        return "WeeklySummary{" +
                "weeklyTotalCalories=" + weeklyTotalCalories +
                ", weeklyTotalProtein=" + weeklyTotalProtein +
                ", weeklyTotalCarbs=" + weeklyTotalCarbs +
                ", weeklyTotalFat=" + weeklyTotalFat +
                ", weeklyTotalCost=" + weeklyTotalCost +
                ", budgetMet=" + budgetMet +
                ", weeklyGoalsMet=" + weeklyGoalsMet +
                ", ingredientsUsed=" + ingredientUsageSummary.size() +
                '}';
    }
}