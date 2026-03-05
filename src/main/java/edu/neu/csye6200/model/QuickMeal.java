package edu.neu.csye6200.model;

import java.util.List;

/**
 * Quick meal with a prep time category (5, 15, 30 minutes).
 */
public class QuickMeal extends Meal {

    private int prepTimeCategory; // 5, 15, 30

    public QuickMeal() {
        super();
    }

    public QuickMeal(String id,
                     String name,
                     String mealType,
                     String dietaryPreference,
                     int calories,
                     double protein,
                     double carbs,
                     double fat,
                     double cost,
                     List<Ingredient> ingredients,
                     boolean favoriteFlag,
                     int prepTimeCategory) {

        super(id, name, mealType, dietaryPreference,
                calories, protein, carbs, fat, cost,
                ingredients, favoriteFlag);
        this.prepTimeCategory = prepTimeCategory;
    }

    public int getPrepTimeCategory() {
        return prepTimeCategory;
    }

    public void setPrepTimeCategory(int prepTimeCategory) {
        this.prepTimeCategory = prepTimeCategory;
    }

    @Override
    public boolean isQuickMeal() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString()
                + " QuickMeal{prepTimeCategory=" + prepTimeCategory + "}";
    }
}