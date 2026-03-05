package edu.neu.csye6200.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a shopping list made up of multiple shopping items.
 */
public class ShoppingList {

    // Internal list of shopping items
    private final List<ShoppingItem> items = new ArrayList<>();

    public List<ShoppingItem> getItems() {
        return items;
    }

    /**
     * Returns an unmodifiable view of items.
     */
    public List<ShoppingItem> getItemsView() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds an item to the shopping list.
     */
    public void addItem(ShoppingItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Clears the shopping list.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Computes the total cost by summing up all item total costs.
     */
    public double getTotalCost() {
        return items.stream()
                .mapToDouble(ShoppingItem::getTotalCost)
                .sum();
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "items=" + items +
                '}';
    }
}
