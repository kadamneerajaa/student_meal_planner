package edu.neu.csye6200.data;

import edu.neu.csye6200.model.Ingredient;
import edu.neu.csye6200.model.Pantry;

import java.io.*;

/**
 * Repository class responsible for loading and saving pantry data to a CSV file.
 *
 * Expected CSV format (with header):
 * name,unit,quantity,costPerUnit
 */
public class PantryCsvRepository {

    // Relative path to the pantry CSV file
    private static final String PANTRY_FILE = "data/pantry.csv";

    /**
     * Loads pantry data from CSV. If file does not exist, returns an empty pantry.
     */
    public Pantry loadPantry() {
        Pantry pantry = new Pantry();
        File file = new File(PANTRY_FILE);

        if (!file.exists()) {
            return pantry;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            // Skip header line if present
            if (line != null && line.toLowerCase().startsWith("name,")) {
                line = reader.readLine();
            }

            while (line != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String name = tokens[0].trim();
                    String unit = tokens[1].trim();

                    double quantity = parseDouble(tokens[2].trim());
                    double costPerUnit = parseDouble(tokens[3].trim());

                    Ingredient ingredient = new Ingredient(name, unit, quantity, costPerUnit);
                    pantry.addOrUpdateIngredient(ingredient);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Simple error reporting; this can be improved later if needed
            System.err.println("Error loading pantry from CSV: " + e.getMessage());
        }

        return pantry;
    }

    /**
     * Saves the given pantry to CSV. Overwrites the file.
     */
    public void savePantry(Pantry pantry) {
        File file = new File(PANTRY_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("name,unit,quantity,costPerUnit");
            for (Ingredient ingredient : pantry.getIngredientsView()) {
                writer.printf("%s,%s,%.4f,%.4f%n",
                        ingredient.getName(),
                        ingredient.getUnit(),
                        ingredient.getQuantity(),
                        ingredient.getCostPerUnit());
            }
        } catch (IOException e) {
            System.err.println("Error saving pantry to CSV: " + e.getMessage());
        }
    }

    /**
     * Helper method to safely parse doubles.
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}
