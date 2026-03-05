package edu.neu.csye6200.service;

import edu.neu.csye6200.model.DayPlan;
import edu.neu.csye6200.model.DailySummary;
import edu.neu.csye6200.model.WeeklyPlan;
import edu.neu.csye6200.model.WeeklySummary;
import edu.neu.csye6200.model.Student;
import edu.neu.csye6200.model.Ingredient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReportService - Generates daily and weekly summaries
 * Demonstrates Java Streams & Lambda expressions for aggregation
 */
public class ReportService {
    
    /**
     * Generates a weekly summary for the given plan and student
     */
    public WeeklySummary generateWeeklySummary(WeeklyPlan plan, Student student) {
        WeeklySummary summary = new WeeklySummary(plan);
        
        // Aggregate ingredient usage across the week
        Map<String, Double> ingredientUsage = aggregateIngredientUsage(plan);
        summary.setIngredientUsageSummary(ingredientUsage);
        
        return summary;
    }
    
    /**
     * Generates a daily summary for the given day plan and student
     */
    public DailySummary generateDailySummary(DayPlan dayPlan, Student student) {
        return new DailySummary(dayPlan, student);
    }
    
    /**
     * Checks if daily goals are achieved
     */
    public boolean checkGoalAchievement(DailySummary summary, Student student) {
        return summary.isCalorieGoalMet() && summary.isMacroGoalsMet();
    }
    
    /**
     * Aggregates ingredient usage across the entire week
     * Uses Java Streams to group ingredients and sum quantities
     */
    public Map<String, Double> aggregateIngredientUsage(WeeklyPlan plan) {
        // Use Java Streams to aggregate all ingredients across the week
        return plan.getDays().stream()
            .flatMap(day -> day.getAllMeals().stream())
            .flatMap(meal -> meal.getIngredients().stream())
            .collect(Collectors.groupingBy(
                Ingredient::getName,
                Collectors.summingDouble(Ingredient::getQuantity)
            ));
    }
    
    /**
     * Calculates weekly averages (per day)
     */
    public Map<String, Double> calculateWeeklyAverages(WeeklySummary summary) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("avgCaloriesPerDay", summary.getWeeklyTotalCalories() / 7.0);
        averages.put("avgCostPerDay", summary.getWeeklyTotalCost() / 7.0);
        averages.put("avgProteinPerDay", summary.getWeeklyTotalProtein() / 7.0);
        averages.put("avgCarbsPerDay", summary.getWeeklyTotalCarbs() / 7.0);
        averages.put("avgFatPerDay", summary.getWeeklyTotalFat() / 7.0);
        return averages;
    }
    
    /**
     * Generates a comprehensive report for all days in the week
     */
    public Map<String, DailySummary> generateAllDailySummaries(WeeklyPlan plan, Student student) {
        Map<String, DailySummary> dailySummaries = new HashMap<>();
        
        for (DayPlan day : plan.getDays()) {
            DailySummary summary = generateDailySummary(day, student);
            dailySummaries.put(day.getDayName(), summary);
        }
        
        return dailySummaries;
    }
    
    /**
     * Calculates the percentage of weekly budget used
     */
    public double calculateBudgetUsagePercentage(WeeklySummary summary, Student student) {
        return (summary.getWeeklyTotalCost() / student.getWeeklyBudget()) * 100.0;
    }
    
    /**
     * Generates a simple text report of the weekly summary
     */
    public String generateWeeklyReportText(WeeklySummary summary, Student student) {
        StringBuilder report = new StringBuilder();
        report.append("=== Weekly Meal Plan Report ===\n");
        report.append("Student: ").append(student.getName()).append("\n");
        report.append("Week Total Calories: ").append(String.format("%.0f", summary.getWeeklyTotalCalories())).append("\n");
        report.append("Week Total Cost: $").append(String.format("%.2f", summary.getWeeklyTotalCost())).append("\n");
        report.append("Budget Status: ").append(summary.isBudgetMet() ? "Within Budget" : "Over Budget").append("\n");
        report.append("Goals Met: ").append(summary.isWeeklyGoalsMet() ? "Yes" : "No").append("\n");
        return report.toString();
    }
}