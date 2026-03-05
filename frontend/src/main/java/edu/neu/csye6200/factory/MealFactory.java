package edu.neu.csye6200.factory;

import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.model.QuickMeal;
import edu.neu.csye6200.model.RegularMeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory pattern for creating Meal objects and handling CSV conversions.
 *
 * CSV column order (meals.csv):
 * 0 id
 * 1 name
 * 2 mealType
 * 3 dietaryPreference
 * 4 calories
 * 5 protein
 * 6 carbs
 * 7 fat
 * 8 cost
 * 9 ingredients (pipe-separated: "Oats|Milk|Banana")
 * 10 favoriteFlag
 * 11 prepTimeCategory (blank for regular meals)
 */
public class MealFactory {

    private static final String INGREDIENT_SEPARATOR = "\\|";
    private static final String INGREDIENT_JOINER = "|";

    private MealFactory() {
        // utility class
    }

    /**
     * Create a Meal based on type.
     *
     * @param type              "REGULAR" or "QUICK"
     * @param id                id
     * @param name              name
     * @param mealType          meal type (BREAKFAST, LUNCH, ...)
     * @param dietaryPreference dietary preference
     * @param calories          calories
     * @param protein           protein
     * @param carbs             carbs
     * @param fat               fat
     * @param cost              cost
     * @param ingredients       ingredient list
     * @param favoriteFlag      favorite flag
     * @param prepTimeCategory  prep-time category (only for QUICK)
     */
    public static Meal createMeal(String type,
                                  String id,
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
                                  Integer prepTimeCategory) {

        if ("QUICK".equalsIgnoreCase(type)) {
            if (prepTimeCategory == null) {
                throw new IllegalArgumentException("QuickMeal requires prepTimeCategory");
            }
            return new QuickMeal(id, name, mealType, dietaryPreference,
                    calories, protein, carbs, fat, cost,
                    ingredients, favoriteFlag, prepTimeCategory);
        }

        // default to regular meal
        return new RegularMeal(id, name, mealType, dietaryPreference,
                calories, protein, carbs, fat, cost,
                ingredients, favoriteFlag);
    }

    /**
     * Parse a CSV row into a Meal.
     */
    public static Meal createFromCsv(String[] row) {
        if (row == null || row.length < 11) {
            throw new IllegalArgumentException("Invalid meal CSV row: " + Arrays.toString(row));
        }

        String id = row[0];
        String name = row[1];
        String mealType = row[2];
        String dietaryPref = row[3];
        int calories = Integer.parseInt(row[4]);
        double protein = Double.parseDouble(row[5]);
        double carbs = Double.parseDouble(row[6]);
        double fat = Double.parseDouble(row[7]);
        double cost = Double.parseDouble(row[8]);
        String ingredientsStr = row[9];
        boolean favorite = Boolean.parseBoolean(row[10]);
        Integer prepTimeCategory = null;

        if (row.length > 11 && row[11] != null && !row[11].isBlank()) {
            prepTimeCategory = Integer.parseInt(row[11]);
        }

        List<Ingredient> ingredients = parseIngredients(ingredientsStr);

        String type = (prepTimeCategory == null) ? "REGULAR" : "QUICK";

        return createMeal(type, id, name, mealType, dietaryPref,
                calories, protein, carbs, fat, cost,
                ingredients, favorite, prepTimeCategory);
    }

    /**
     * Convert a Meal into a CSV row (String array).
     */
    public static String[] toCsvRow(Meal meal) {
        List<String> row = new ArrayList<>();

        row.add(meal.getId());
        row.add(meal.getName());
        row.add(meal.getMealType());
        row.add(meal.getDietaryPreference());
        row.add(String.valueOf(meal.getCalories()));
        row.add(String.valueOf(meal.getProtein()));
        row.add(String.valueOf(meal.getCarbs()));
        row.add(String.valueOf(meal.getFat()));
        row.add(String.valueOf(meal.getCost()));

        row.add(ingredientsToString(meal.getIngredients()));
        row.add(String.valueOf(meal.isFavoriteFlag()));

        int prepTime = meal.getPrepTimeCategory();
        row.add(prepTime > 0 ? String.valueOf(prepTime) : "");

        return row.toArray(new String[0]);
    }

    // --- Helpers for ingredients representation ------------------------------

    /**
     * Parse pipe-separated ingredient names into a list of Ingredient objects.
     * Only the name is populated; other fields can be filled later by Team A.
     */
    private static List<Ingredient> parseIngredients(String ingredientsStr) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsStr == null || ingredientsStr.isBlank()) {
            return ingredients;
        }

        String[] tokens = ingredientsStr.split(INGREDIENT_SEPARATOR);
        for (String token : tokens) {
            String name = token.trim();
            if (!name.isEmpty()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(name); // assumes setter exists in Ingredient.java
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    /**
     * Convert ingredient list to pipe-separated names.
     */
    private static String ingredientsToString(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }
        return ingredients.stream()
                .map(Ingredient::getName)
                .map(s -> s == null ? "" : s.replace(",", ""))
                .collect(Collectors.joining(INGREDIENT_JOINER));
    }
}