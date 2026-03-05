package edu.neu.csye6200.model;

/**
 * Represents a single ingredient with a name, unit, quantity and cost per unit.
 */
public class Ingredient {

    // Ingredient name, for example "Chicken Breast"
    private String name;

    // Measurement unit, for example "g", "lb", "pcs"
    private String unit;

    // Quantity in the given unit
    private double quantity;

    // Cost per unit in the given unit (for example $/lb)
    private double costPerUnit;

    public Ingredient() {
        // No-arg constructor for frameworks and simple instantiation
    }

    public Ingredient(String name, String unit, double quantity, double costPerUnit) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.costPerUnit = costPerUnit;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    /**
     * Computes the total value of this ingredient based on quantity and cost per unit.
     */
    public double getTotalValue() {
        return quantity * costPerUnit;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", costPerUnit=" + costPerUnit +
                '}';
    }
}
