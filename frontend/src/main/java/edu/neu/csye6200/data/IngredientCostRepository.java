package edu.neu.csye6200.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository class responsible for loading and saving ingredient costs
 * from and to a CSV file.
 *
 * CSV format (with header):
 * ingredientName,costPerUnit,unit
 */
public class IngredientCostRepository {

    // Relative path to the ingredient cost CSV file
    private static final String COST_FILE = "data/ingredient_costs.csv";

    // Maps normalized ingredient name to its cost per unit
    private final Map<String, Double> costPerUnitByName = new HashMap<>();

    // Maps normalized ingredient name to its unit
    private final Map<String, String> unitByName = new HashMap<>();

    /**
     * Loads ingredient cost data from CSV into memory.
     * If the file does not exist, it is created with only a header row.
     */
    public void loadCosts() {
        costPerUnitByName.clear();
        unitByName.clear();

        File file = new File(COST_FILE);

        if (!file.exists()) {
            createEmptyCostFile(file);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            // Skip header if present
            if (line != null && line.toLowerCase().startsWith("ingredientname")) {
                line = reader.readLine();
            }

            while (line != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String name = tokens[0].trim();
                    double costPerUnit = parseDouble(tokens[1].trim());
                    String unit = tokens[2].trim();

                    String key = normalizeName(name);
                    costPerUnitByName.put(key, costPerUnit);
                    unitByName.put(key, unit);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error loading ingredient costs from CSV: " + e.getMessage());
        }
    }

    /**
     * Adds or updates the cost information for an ingredient
     * and immediately saves all costs back to the CSV file.
     */
    public void addOrUpdateCost(String ingredientName, double costPerUnit, String unit) {
        if (ingredientName == null || unit == null) {
            return;
        }

        String key = normalizeName(ingredientName);
        costPerUnitByName.put(key, costPerUnit);
        unitByName.put(key, unit);

        saveCosts();
    }

    /**
     * Returns the cost per unit for the given ingredient name, or 0.0 if unknown.
     */
    public double getCostPerUnit(String ingredientName) {
        if (ingredientName == null) {
            return 0.0;
        }
        String key = normalizeName(ingredientName);
        return costPerUnitByName.getOrDefault(key, 0.0);
    }

    /**
     * Returns the preferred unit for the given ingredient name, or null if unknown.
     */
    public String getUnit(String ingredientName) {
        if (ingredientName == null) {
            return null;
        }
        String key = normalizeName(ingredientName);
        return unitByName.get(key);
    }

    /**
     * Saves all cost entries back to the CSV file.
     * This overwrites the file with a header row and all known costs.
     */
    public void saveCosts() {
        File file = new File(COST_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("ingredientName,costPerUnit,unit");
            for (Map.Entry<String, Double> entry : costPerUnitByName.entrySet()) {
                String key = entry.getKey();
                String unit = unitByName.getOrDefault(key, "");
                double costPerUnit = entry.getValue();
                writer.printf("%s,%.4f,%s%n", key, costPerUnit, unit);
            }
        } catch (IOException e) {
            System.err.println("Error saving ingredient costs to CSV: " + e.getMessage());
        }
    }

    /**
     * Creates an empty ingredient_costs.csv file with only the header line.
     */
    private void createEmptyCostFile(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("ingredientName,costPerUnit,unit");
        } catch (IOException e) {
            System.err.println("Error creating empty ingredient costs CSV: " + e.getMessage());
        }
    }

    /**
     * Normalizes ingredient name to support case-insensitive lookup.
     */
    private String normalizeName(String name) {
        return name.trim().toLowerCase();
    }

    /**
     * Helps avoid NumberFormatException when parsing doubles.
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}
