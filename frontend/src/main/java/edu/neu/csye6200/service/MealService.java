package edu.neu.csye6200.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.neu.csye6200.data.MealCsvRepository;
import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Meal;
import edu.neu.csye6200.model.QuickMeal;

public class MealService {

    private final MealCsvRepository repository;
    private final List<Meal> meals;

    public MealService() {
        this(new MealCsvRepository());
    }

    public MealService(MealCsvRepository repository) {
        this.repository = repository;
        this.meals = new ArrayList<>(repository.loadMeals());
        
        // Assign default ingredients to each meal
        for (Meal meal : this.meals) {
            assignDefaultIngredients(meal);
        }
        
        System.out.println("Loaded " + meals.size() + " meals with ingredients");
    }

    // --- CRUD ---------------------------------------------------------------

    public List<Meal> getAllMeals() {
        return new ArrayList<>(meals);
    }

    public Meal getMealById(String id) {
        return findById(id).orElse(null);
    }

    public Meal createMeal(Meal meal) {
    if (meal.getId() == null || meal.getId().isBlank()) {
        meal.setId(generateId());
    }
    
    // Only assign default ingredients if meal has none
    if (meal.getIngredients() == null || meal.getIngredients().isEmpty()) {
        System.out.println("[MealService] No ingredients provided, assigning defaults");
        assignDefaultIngredients(meal);
    } else {
        System.out.println("[MealService] Meal already has " + meal.getIngredients().size() + " ingredients");
    }
    
    meals.add(meal);
    persist();
    return meal;
}

    public Meal updateMeal(Meal updated) {
    if (updated == null || updated.getId() == null) {
        throw new IllegalArgumentException("Meal id required for update");
    }

    System.out.println("[MealService] Updating meal with ID: " + updated.getId());

    Optional<Meal> existingOpt = findById(updated.getId());
    if (existingOpt.isEmpty()) {
        System.out.println("[MealService] Meal not found - ERROR!");
        throw new IllegalArgumentException("Cannot update non-existent meal: " + updated.getId());
    }

    Meal existing = existingOpt.get();
    System.out.println("[MealService] Found existing meal: " + existing.getName());
    
    int index = meals.indexOf(existing);
    
    // Use the updated ingredients if provided
    if (updated.getIngredients() == null || updated.getIngredients().isEmpty()) {
        System.out.println("[MealService] No ingredients in update, preserving existing");
        updated.setIngredients(existing.getIngredients());
    }
    
    meals.set(index, updated);
    System.out.println("[MealService] Updated meal at index: " + index);
    
    persist();
    return updated;
}

    public boolean deleteMeal(String id) {
        Optional<Meal> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }
        meals.remove(existingOpt.get());
        persist();
        return true;
    }

    // --- Search / Sort ------------------------------------------------------

    public List<Meal> searchMealsByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllMeals();
        }
        String lower = name.toLowerCase(Locale.ROOT);
        return meals.stream()
                .filter(m -> m.getName() != null &&
                        m.getName().toLowerCase(Locale.ROOT).contains(lower))
                .collect(Collectors.toList());
    }

    public List<Meal> sortMeals(String criteria) {
        if (criteria == null || criteria.isBlank()) {
            return getAllMeals();
        }

        String crit = criteria.toLowerCase(Locale.ROOT);
        Comparator<Meal> comparator;

        switch (crit) {
            case "calories":
                comparator = Comparator.comparingInt(Meal::getCalories);
                break;
            case "cost":
                comparator = Comparator.comparingDouble(Meal::getCost);
                break;
            case "protein":
                comparator = Comparator.comparingDouble(Meal::getProtein);
                break;
            case "carbs":
                comparator = Comparator.comparingDouble(Meal::getCarbs);
                break;
            case "fat":
                comparator = Comparator.comparingDouble(Meal::getFat);
                break;
            case "name":
            default:
                comparator = Comparator.comparing(
                        m -> m.getName() == null ? "" : m.getName().toLowerCase(Locale.ROOT));
                break;
        }

        return meals.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    // --- Favorites & Filters -------------------------------------------------

    public List<Meal> getFavoriteMeals() {
        return meals.stream()
                .filter(Meal::isFavoriteFlag)
                .collect(Collectors.toList());
    }

    public void setFavorite(String id, boolean favorite) {
        findById(id).ifPresent(meal -> {
            meal.setFavoriteFlag(favorite);
            persist();
        });
    }

    public List<Meal> getMealsByAttribute(String mealType, boolean favorite) {
        String typeLower = mealType == null ? "" : mealType.toLowerCase(Locale.ROOT);

        return meals.stream()
                .filter(m -> typeLower.isEmpty()
                        || (m.getMealType() != null
                        && m.getMealType().toLowerCase(Locale.ROOT).equals(typeLower)))
                .filter(m -> !favorite || m.isFavoriteFlag())
                .collect(Collectors.toList());
    }

    public List<QuickMeal> getQuickMeals(int prepTime) {
        return meals.stream()
                .filter(Meal::isQuickMeal)
                .map(m -> (QuickMeal) m)
                .filter(q -> q.getPrepTimeCategory() <= prepTime)
                .collect(Collectors.toList());
    }

    // --- Ingredient Assignment ----------------------------------------------
    
    /**
     * Assigns default ingredients to meals based on their names.
     */
    private void assignDefaultIngredients(Meal meal) {
        List<Ingredient> ingredients = new ArrayList<>();
        String mealName = meal.getName().toLowerCase();
        
        // Breakfast items
        if (mealName.contains("oatmeal")) {
            ingredients.add(new Ingredient("Oats", "cup", 1.0, 2.0));
            ingredients.add(new Ingredient("Milk", "cup", 0.5, 1.5));
            if (mealName.contains("berries")) {
                ingredients.add(new Ingredient("Berries", "cup", 0.5, 3.0));
            }
        } else if (mealName.contains("scrambled eggs")) {
            ingredients.add(new Ingredient("Eggs", "count", 2.0, 0.5));
            ingredients.add(new Ingredient("Bread", "slices", 2.0, 0.5));
            ingredients.add(new Ingredient("Butter", "tbsp", 1.0, 0.5));
        } else if (mealName.contains("rice") && !mealName.contains("salmon")) {
            ingredients.add(new Ingredient("Rice", "cup", 1.0, 1.0));
        }
        
        // Lunch items
        else if (mealName.contains("chicken") && mealName.contains("salad")) {
            ingredients.add(new Ingredient("Chicken Breast", "lbs", 0.5, 4.0));
            ingredients.add(new Ingredient("Lettuce", "cup", 2.0, 1.0));
            ingredients.add(new Ingredient("Tomatoes", "count", 2.0, 1.5));
        } else if (mealName.contains("grilled chicken")) {
            ingredients.add(new Ingredient("Chicken Breast", "lbs", 0.5, 4.0));
            ingredients.add(new Ingredient("Olive Oil", "tbsp", 1.0, 0.5));
        }
        
        // Dinner items
        else if (mealName.contains("salmon")) {
            ingredients.add(new Ingredient("Salmon", "lbs", 0.5, 8.0));
            ingredients.add(new Ingredient("Rice", "cup", 1.0, 1.0));
            ingredients.add(new Ingredient("Lemon", "count", 1.0, 0.5));
        }
        
        // Snacks
        else if (mealName.contains("protein shake")) {
            ingredients.add(new Ingredient("Protein Powder", "scoops", 1.0, 2.0));
            ingredients.add(new Ingredient("Milk", "cup", 1.0, 1.5));
            ingredients.add(new Ingredient("Banana", "count", 1.0, 0.5));
        }
        
        // Maggie/noodles
        else if (mealName.contains("maggie") || mealName.contains("magge")) {
            ingredients.add(new Ingredient("Maggie Noodles", "packet", 1.0, 1.0));
            ingredients.add(new Ingredient("Vegetables", "cup", 0.5, 1.0));
        }
        
        meal.setIngredients(ingredients);
        System.out.println("Assigned " + ingredients.size() + " ingredients to: " + meal.getName());
    }

    // --- Helper methods ------------------------------------------------------

    private Optional<Meal> findById(String id) {
        if (id == null) return Optional.empty();
        return meals.stream()
                .filter(m -> id.equals(m.getId()))
                .findFirst();
    }

    private void persist() {
        repository.saveMeals(meals);
    }

    private String generateId() {
        int next = meals.size() + 1;
        return "M" + next;
    }
}