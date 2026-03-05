package edu.neu.csye6200.data;

import edu.neu.csye6200.factory.MealFactory;
import edu.neu.csye6200.model.Meal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV repository for meals.
 *
 * File: data/meals.csv
 */
public class MealCsvRepository {

    private static final String[] HEADER = {
            "id", "name", "mealType", "dietaryPreference",
            "calories", "protein", "carbs", "fat", "cost",
            "ingredients", "favoriteFlag", "prepTimeCategory"
    };

    private final Path filePath;

    public MealCsvRepository() {
        this(Paths.get("data", "meals.csv"));
    }

    public MealCsvRepository(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Load all meals from CSV.
     */
    public List<Meal> loadMeals() {
        List<Meal> meals = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return meals;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (first) { // skip header
                    first = false;
                    continue;
                }
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1); // keep empty trailing columns
                Meal meal = MealFactory.createFromCsv(cols);
                meals.add(meal);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load meals from CSV: " + filePath, e);
        }

        return meals;
    }

    /**
     * Save all meals to CSV (overwrites existing file).
     */
    public void saveMeals(List<Meal> meals) {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory for meals CSV", e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            // header
            writer.write(String.join(",", HEADER));
            writer.newLine();

            if (meals != null) {
                for (Meal meal : meals) {
                    String[] cols = MealFactory.toCsvRow(meal);
                    writer.write(String.join(",", cols));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save meals to CSV: " + filePath, e);
        }
    }
}