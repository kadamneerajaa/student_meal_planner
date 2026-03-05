package edu.neu.csye6200.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a Meal in the library.
 *
 * Fields from spec:
 * id, name, mealType, dietaryPreference, calories, protein, carbs, fat, cost,
 * ingredients (List<Ingredient>), favoriteFlag
 */
public abstract class Meal {

    private String id;
    private String name;
    private String mealType;           // e.g. BREAKFAST, LUNCH, DINNER, SNACK
    private String dietaryPreference;  // e.g. VEGAN, VEGETARIAN, NONE

    private int calories;
    private double protein;
    private double carbs;
    private double fat;
    private double cost;

    private List<Ingredient> ingredients = new ArrayList<>();
    private boolean favoriteFlag;

    protected Meal() {
        // no-arg constructor for JSON / CSV frameworks
    }

    protected Meal(String id,
                   String name,
                   String mealType,
                   String dietaryPreference,
                   int calories,
                   double protein,
                   double carbs,
                   double fat,
                   double cost,
                   List<Ingredient> ingredients,
                   boolean favoriteFlag) {

        this.id = id;
        this.name = name;
        this.mealType = mealType;
        this.dietaryPreference = dietaryPreference;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.cost = cost;
        if (ingredients != null) {
            this.ingredients = new ArrayList<>(ingredients);
        }
        this.favoriteFlag = favoriteFlag;
    }

    // --- Abstract hooks ------------------------------------------------------

    /**
     * @return true if this is a quick meal (with prep time category).
     */
    public abstract boolean isQuickMeal();

    /**
     * Default implementation: regular meals have no prep time category.
     * QuickMeal overrides.
     */
    public int getPrepTimeCategory() {
        return -1;
    }

    // --- Getters / Setters ---------------------------------------------------

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

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = (ingredients == null)
                ? new ArrayList<>()
                : new ArrayList<>(ingredients);
    }

    public boolean isFavoriteFlag() {
        return favoriteFlag;
    }

    public void setFavoriteFlag(boolean favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }

    // --- Utility -------------------------------------------------------------

    public void addIngredient(Ingredient ingredient) {
        if (ingredient != null) {
            this.ingredients.add(ingredient);
        }
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mealType='" + mealType + '\'' +
                ", dietaryPreference='" + dietaryPreference + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fat=" + fat +
                ", cost=" + cost +
                ", favoriteFlag=" + favoriteFlag +
                ", quick=" + isQuickMeal() +
                ", prepTime=" + getPrepTimeCategory() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal)) return false;
        Meal meal = (Meal) o;
        return Objects.equals(id, meal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}