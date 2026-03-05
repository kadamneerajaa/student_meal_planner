package edu.neu.csye6200.model;

/**
 * Represents a single item on the shopping list.
 * Contains the ingredient name, unit, required quantity, cost per unit and total cost.
 */
public class ShoppingItem {

    // Ingredient name
    private String name;

    // Measurement unit (for example "g", "lb", "pcs")
    private String unit;

    // Quantity that needs to be purchased
    private double requiredQuantity;

    // Cost per unit from the cost repository
    private double costPerUnit;

    // Total cost for this shopping item
    private double totalCost;

    public ShoppingItem() {
        // No-arg constructor
    }

    public ShoppingItem(String name, String unit, double requiredQuantity,
                        double costPerUnit, double totalCost) {
        this.name = name;
        this.unit = unit;
        this.requiredQuantity = requiredQuantity;
        this.costPerUnit = costPerUnit;
        this.totalCost = totalCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
        recalculateTotalCost();
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
        recalculateTotalCost();
    }

    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Recalculates total cost based on quantity and cost per unit.
     */
    private void recalculateTotalCost() {
        this.totalCost = this.requiredQuantity * this.costPerUnit;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", requiredQuantity=" + requiredQuantity +
                ", costPerUnit=" + costPerUnit +
                ", totalCost=" + totalCost +
                '}';
    }
}
