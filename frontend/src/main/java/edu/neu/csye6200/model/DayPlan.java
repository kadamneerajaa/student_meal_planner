package edu.neu.csye6200.model;

import java.util.ArrayList;
import java.util.List;

public class DayPlan {
    private String dayName; // Monday, Tuesday, etc.
    private Meal breakfast;
    private Meal lunch;
    private Meal dinner;
    private List<Meal> quickMeals;
    
    public DayPlan(String dayName) {
        this.dayName = dayName;
        this.quickMeals = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getDayName() {
        return dayName;
    }
    
    public void setDayName(String dayName) {
        this.dayName = dayName;
    }
    
    public Meal getBreakfast() {
        return breakfast;
    }
    
    public void setBreakfast(Meal breakfast) {
        this.breakfast = breakfast;
    }
    
    public Meal getLunch() {
        return lunch;
    }
    
    public void setLunch(Meal lunch) {
        this.lunch = lunch;
    }
    
    public Meal getDinner() {
        return dinner;
    }
    
    public void setDinner(Meal dinner) {
        this.dinner = dinner;
    }
    
    public List<Meal> getQuickMeals() {
        return quickMeals;
    }
    
    public void setQuickMeals(List<Meal> quickMeals) {
        this.quickMeals = quickMeals;
    }
    
    // Calculate daily totals using Java Streams
    public double calculateDailyCalories() {
        double total = 0;
        if (breakfast != null) total += breakfast.getCalories();
        if (lunch != null) total += lunch.getCalories();
        if (dinner != null) total += dinner.getCalories();
        total += quickMeals.stream()
            .mapToDouble(Meal::getCalories)
            .sum();
        return total;
    }
    
    public double calculateDailyCost() {
        double total = 0;
        if (breakfast != null) total += breakfast.getCost();
        if (lunch != null) total += lunch.getCost();
        if (dinner != null) total += dinner.getCost();
        total += quickMeals.stream()
            .mapToDouble(Meal::getCost)
            .sum();
        return total;
    }
    
    public double calculateDailyProtein() {
        double total = 0;
        if (breakfast != null) total += breakfast.getProtein();
        if (lunch != null) total += lunch.getProtein();
        if (dinner != null) total += dinner.getProtein();
        total += quickMeals.stream()
            .mapToDouble(Meal::getProtein)
            .sum();
        return total;
    }
    
    public double calculateDailyCarbs() {
        double total = 0;
        if (breakfast != null) total += breakfast.getCarbs();
        if (lunch != null) total += lunch.getCarbs();
        if (dinner != null) total += dinner.getCarbs();
        total += quickMeals.stream()
            .mapToDouble(Meal::getCarbs)
            .sum();
        return total;
    }
    
    public double calculateDailyFat() {
        double total = 0;
        if (breakfast != null) total += breakfast.getFat();
        if (lunch != null) total += lunch.getFat();
        if (dinner != null) total += dinner.getFat();
        total += quickMeals.stream()
            .mapToDouble(Meal::getFat)
            .sum();
        return total;
    }
    
    public List<Meal> getAllMeals() {
        List<Meal> allMeals = new ArrayList<>();
        if (breakfast != null) allMeals.add(breakfast);
        if (lunch != null) allMeals.add(lunch);
        if (dinner != null) allMeals.add(dinner);
        allMeals.addAll(quickMeals);
        return allMeals;
    }
}