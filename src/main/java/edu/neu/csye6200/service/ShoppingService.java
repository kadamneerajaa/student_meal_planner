package edu.neu.csye6200.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.neu.csye6200.data.IngredientCostRepository;
import edu.neu.csye6200.data.PantryCsvRepository;
import edu.neu.csye6200.model.DayPlan;
import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.model.Pantry;
import edu.neu.csye6200.model.ShoppingItem;
import edu.neu.csye6200.model.ShoppingList;
import edu.neu.csye6200.model.WeeklyPlan;

/**
 * Service class responsible for:
 * - Managing the pantry (add, update, remove ingredients)
 * - Generating the shopping list from a weekly plan and pantry
 * - Calculating total shopping cost
 * - Comparing the total cost against the student's weekly budget
 */
public class ShoppingService {

    private final PantryCsvRepository pantryRepository;
    private final IngredientCostRepository costRepository;

    // Current pantry loaded from CSV and kept in memory
    private Pantry pantry;

    public ShoppingService(PantryCsvRepository pantryRepository,
                           IngredientCostRepository costRepository) {
        this.pantryRepository = pantryRepository;
        this.costRepository = costRepository;
        this.pantry = pantryRepository.loadPantry();
        this.costRepository.loadCosts();
    }

    /**
     * Returns the current pantry.
     */
    public Pantry getPantry() {
        return pantry;
    }

    /**
     * Adds a new ingredient to the pantry and persists it.
     */
    public void addToPantry(Ingredient ingredient) throws IllegalArgumentException {
    if (ingredient == null || ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
        throw new IllegalArgumentException("Ingredient name cannot be empty");
    }
    
    // Check for duplicates (case-insensitive)
    Optional<Ingredient> existing = pantry.findByName(ingredient.getName());
    if (existing.isPresent()) {
        throw new IllegalArgumentException("Item '" + ingredient.getName() + "' already exists. Please edit the existing item instead.");
    }
    
    pantry.addOrUpdateIngredient(ingredient);
    pantryRepository.savePantry(pantry);
}
    /**
     * Updates an existing pantry item (same name) and persists the change.
     */
    public void updatePantryItem(Ingredient ingredient) {
        pantry.addOrUpdateIngredient(ingredient);
        pantryRepository.savePantry(pantry);
    }

    /**
     * Removes a pantry item by name and persists the change.
     */
    public void removePantryItem(String name) {
        pantry.removeByName(name);
        pantryRepository.savePantry(pantry);
    }

    /**
     * Generates a shopping list for the given weekly plan using the current pantry.
     */
    public ShoppingList generateShoppingList(WeeklyPlan weeklyPlan) {
        return generateShoppingList(weeklyPlan, this.pantry);
    }

    /**
     * Generates a shopping list for the given weekly plan and pantry.
     * The method:
     *  - Aggregates all ingredients required by the weekly plan
     *  - Subtracts quantities that already exist in the pantry
     *  - Creates ShoppingItem objects for the remaining quantities
     */
    public ShoppingList generateShoppingList(WeeklyPlan weeklyPlan, Pantry pantry) {
        Map<String, Ingredient> requiredIngredients = collectRequiredIngredients(weeklyPlan);

        // Subtract pantry quantities
        if (pantry != null) {
            for (Ingredient pantryIngredient : pantry.getIngredientsView()) {
                String key = makeKey(pantryIngredient.getName(), pantryIngredient.getUnit());
                Ingredient required = requiredIngredients.get(key);
                if (required != null) {
                    double remaining = required.getQuantity() - pantryIngredient.getQuantity();
                    if (remaining <= 0.0) {
                        requiredIngredients.remove(key);
                    } else {
                        required.setQuantity(remaining);
                    }
                }
            }
        }

        // Build shopping list from remaining required ingredients
        ShoppingList shoppingList = new ShoppingList();
        for (Ingredient needed : requiredIngredients.values()) {
            double costPerUnit = costRepository.getCostPerUnit(needed.getName());
            double totalCost = costPerUnit * needed.getQuantity();
            ShoppingItem item = new ShoppingItem(
                    needed.getName(),
                    needed.getUnit(),
                    needed.getQuantity(),
                    costPerUnit,
                    totalCost
            );
            shoppingList.addItem(item);
        }

        return shoppingList;
    }

    /**
     * Calculates the total cost of a shopping list using Java Streams.
     */
    public double calculateTotalCost(ShoppingList shoppingList) {
        return shoppingList.getItems()
                .stream()
                .mapToDouble(ShoppingItem::getTotalCost)
                .sum();
    }

    /**
     * Returns true if the total shopping cost is within the student budget.
     */
    public boolean isWithinBudget(ShoppingList shoppingList, double weeklyBudget) {
        double totalCost = calculateTotalCost(shoppingList);
        return totalCost <= weeklyBudget;
    }

    /**
     * Aggregates all ingredients required by all meals in the weekly plan.
     */
    private Map<String, Ingredient> collectRequiredIngredients(WeeklyPlan weeklyPlan) {
        Map<String, Ingredient> result = new HashMap<>();

        if (weeklyPlan == null || weeklyPlan.getDays() == null) {
            return result;
        }

        for (DayPlan dayPlan : weeklyPlan.getDays()) {
            if (dayPlan == null) {
                continue;
            }

            List<Meal> allMeals = dayPlan.getAllMeals();
            if (allMeals == null) {
                continue;
            }

            for (Meal meal : allMeals) {
                addMealIngredients(result, meal);
            }
        }

        return result;
    }

    /**
     * Adds ingredients from a single meal into the aggregated map.
     */
    private void addMealIngredients(Map<String, Ingredient> aggregate, Meal meal) {
        if (meal == null || meal.getIngredients() == null) {
            return;
        }

        for (Ingredient ingredient : meal.getIngredients()) {
            String key = makeKey(ingredient.getName(), ingredient.getUnit());
            Ingredient existing = aggregate.get(key);
            if (existing == null) {
                Ingredient copy = new Ingredient(
                        ingredient.getName(),
                        ingredient.getUnit(),
                        ingredient.getQuantity(),
                        ingredient.getCostPerUnit()
                );
                aggregate.put(key, copy);
            } else {
                double newQuantity = existing.getQuantity() + ingredient.getQuantity();
                existing.setQuantity(newQuantity);
            }
        }
    }

    /**
     * Builds a composite key from ingredient name and unit.
     */
    private String makeKey(String name, String unit) {
        String safeName = (name == null) ? "" : name.trim().toLowerCase();
        String safeUnit = (unit == null) ? "" : unit.trim().toLowerCase();
        return safeName + "|" + safeUnit;
    }
}
