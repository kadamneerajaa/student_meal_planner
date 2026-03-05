package edu.neu.csye6200;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import edu.neu.csye6200.data.IngredientCostRepository;
import edu.neu.csye6200.data.MealCsvRepository;
import edu.neu.csye6200.data.PantryCsvRepository;
import edu.neu.csye6200.server.CorsHandler;
import edu.neu.csye6200.server.handlers.MealHandler;
import edu.neu.csye6200.server.handlers.PlanHandler;
import edu.neu.csye6200.server.handlers.ReportHandler;
import edu.neu.csye6200.server.handlers.ShoppingHandler;
import edu.neu.csye6200.server.handlers.StudentHandler;
import edu.neu.csye6200.service.MealService;
import edu.neu.csye6200.service.PlanService;
import edu.neu.csye6200.service.ReportService;
import edu.neu.csye6200.service.ShoppingService;

public class Driver {
    public static void main(String[] args) {
        System.out.println("============ Starting Student Meal Planner Server ============\n");

        try {
            // Initialize repositories
            System.out.println("Initializing repositories...");
            MealCsvRepository mealRepo = new MealCsvRepository();
            PantryCsvRepository pantryRepo = new PantryCsvRepository();
            IngredientCostRepository costRepo = new IngredientCostRepository();
            System.out.println("Repositories initialized");

            // Initialize services
            System.out.println("Initializing services...");
            MealService mealService = new MealService(mealRepo);
            ShoppingService shoppingService = new ShoppingService(pantryRepo, costRepo);
            PlanService planService = new PlanService();
            ReportService reportService = new ReportService();
            System.out.println("Services initialized");

            // Create HTTP server on port 8080
            System.out.println("Creating HTTP server on port 8080...");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Register handlers with CORS support
            System.out.println("Registering API endpoints...");
            server.createContext("/api/meals", new CorsHandler(new MealHandler(mealService)));
            server.createContext("/api/plans", new CorsHandler(new PlanHandler(planService, mealService)));
            server.createContext("/api/shopping", new CorsHandler(new ShoppingHandler(shoppingService, planService)));
            server.createContext("/api/reports", new CorsHandler(new ReportHandler(reportService, planService)));
            server.createContext("/api/student", new CorsHandler(new StudentHandler()));
            System.out.println("API endpoints registered");

            // Start server
            server.setExecutor(null);
            server.start();

            System.out.println("\n============ Server Started Successfully! ============");
            System.out.println(" Server URL: http://localhost:8080");
            System.out.println("API Base URL: http://localhost:8080/api");
            System.out.println("\n Available Endpoints:");
            System.out.println("  GET    http://localhost:8080/api/meals");
            System.out.println("  POST   http://localhost:8080/api/meals");
            System.out.println("  GET    http://localhost:8080/api/plans");
            System.out.println("  POST   http://localhost:8080/api/plans");
            System.out.println("  GET    http://localhost:8080/api/plans/day/{day}");
            System.out.println("  PUT    http://localhost:8080/api/plans/day/{day}");
            System.out.println("  GET    http://localhost:8080/api/student");
            System.out.println("  GET    http://localhost:8080/api/shopping/pantry");
            System.out.println("  POST   http://localhost:8080/api/shopping/pantry");
            System.out.println("  PUT    http://localhost:8080/api/shopping/pantry/{name}");
            System.out.println("  DELETE http://localhost:8080/api/shopping/pantry/{name}");
            System.out.println("  GET    http://localhost:8080/api/shopping/list");

            System.out.println("\n Press Ctrl+C to stop the server");
            System.out.println("=======================================================\n");

        } catch (IOException e) {
            System.err.println("\n Failed to start server!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}