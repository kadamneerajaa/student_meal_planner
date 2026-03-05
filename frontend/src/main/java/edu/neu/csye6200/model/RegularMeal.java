package edu.neu.csye6200.model;

import java.util.List;

/**
 * Regular meal (Breakfast, Lunch, Dinner, etc.).
 */
public class RegularMeal extends Meal {

    public RegularMeal() {
        super();
    }

    public RegularMeal(String id,
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

        super(id, name, mealType, dietaryPreference,
                calories, protein, carbs, fat, cost,
                ingredients, favoriteFlag);
    }

    @Override
    public boolean isQuickMeal() {
        return false;
    }
}