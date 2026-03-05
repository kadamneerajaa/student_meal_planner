package edu.neu.csye6200.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the pantry containing all ingredients currently on hand.
 */
public class Pantry {

    // Internal list of ingredients on hand
    private final List<Ingredient> ingredients = new ArrayList<>();

    /**
     * Returns a modifiable list of ingredients.
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Returns an unmodifiable view of the ingredients.
     */
    public List<Ingredient> getIngredientsView() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Finds an ingredient by name (case-insensitive).
     */
    public Optional<Ingredient> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return ingredients.stream()
                .filter(ing -> name.equalsIgnoreCase(ing.getName()))
                .findFirst();
    }

    /**
     * Adds a new ingredient or updates an existing one if the name matches (case-insensitive).
     */
    public void addOrUpdateIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.getName() == null) {
            return;
        }

        Optional<Ingredient> existingOpt = findByName(ingredient.getName());
        if (existingOpt.isPresent()) {
            Ingredient existing = existingOpt.get();
            existing.setUnit(ingredient.getUnit());
            existing.setQuantity(ingredient.getQuantity());
            existing.setCostPerUnit(ingredient.getCostPerUnit());
        } else {
            ingredients.add(ingredient);
        }
    }

    /**
     * Removes an ingredient by name (case-insensitive).
     */
    public boolean removeByName(String name) {
        if (name == null) {
            return false;
        }
        return ingredients.removeIf(ing -> name.equalsIgnoreCase(ing.getName()));
    }

    /**
     * Clears the pantry.
     */
    public void clear() {
        ingredients.clear();
    }

    @Override
    public String toString() {
        return "Pantry{" +
                "ingredients=" + ingredients +
                '}';
    }
}
