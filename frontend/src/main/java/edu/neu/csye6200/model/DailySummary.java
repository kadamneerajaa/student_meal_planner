package edu.neu.csye6200.model;

public class DailySummary {
    private DayPlan dayPlan;
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double totalCost;
    private boolean calorieGoalMet;
    private boolean macroGoalsMet;
    
    public DailySummary(DayPlan dayPlan, Student student) {
        this.dayPlan = dayPlan;
        calculateTotals();
        checkGoals(student);
    }
    
    private void calculateTotals() {
        this.totalCalories = dayPlan.calculateDailyCalories();
        this.totalProtein = dayPlan.calculateDailyProtein();
        this.totalCarbs = dayPlan.calculateDailyCarbs();
        this.totalFat = dayPlan.calculateDailyFat();
        this.totalCost = dayPlan.calculateDailyCost();
    }
    
    private void checkGoals(Student student) {
        // Compare totals with student goals
        this.calorieGoalMet = Math.abs(totalCalories - student.getDailyCalorieGoal()) 
                             < (student.getDailyCalorieGoal() * 0.1); // 10% tolerance
        
        this.macroGoalsMet = 
            Math.abs(totalProtein - student.getProteinGoal()) < (student.getProteinGoal() * 0.1) &&
            Math.abs(totalCarbs - student.getCarbsGoal()) < (student.getCarbsGoal() * 0.1) &&
            Math.abs(totalFat - student.getFatsGoal()) < (student.getFatsGoal() * 0.1);
    }
    
    // Getters
    public DayPlan getDayPlan() {
        return dayPlan;
    }
    
    public double getTotalCalories() {
        return totalCalories;
    }
    
    public double getTotalProtein() {
        return totalProtein;
    }
    
    public double getTotalCarbs() {
        return totalCarbs;
    }
    
    public double getTotalFat() {
        return totalFat;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public boolean isCalorieGoalMet() {
        return calorieGoalMet;
    }
    
    public boolean isMacroGoalsMet() {
        return macroGoalsMet;
    }
    
    @Override
    public String toString() {
        return "DailySummary{" +
                "day=" + dayPlan.getDayName() +
                ", totalCalories=" + totalCalories +
                ", totalProtein=" + totalProtein +
                ", totalCarbs=" + totalCarbs +
                ", totalFat=" + totalFat +
                ", totalCost=" + totalCost +
                ", calorieGoalMet=" + calorieGoalMet +
                ", macroGoalsMet=" + macroGoalsMet +
                '}';
    }
}