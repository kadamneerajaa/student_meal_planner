package edu.neu.csye6200.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * CORS wrapper handler that adds CORS headers to all responses.
 * This allows the React frontend to communicate with the Java backend.
 */
public class CorsHandler implements HttpHandler {
    
    private final HttpHandler delegate;
    
    public CorsHandler(HttpHandler delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers to allow cross-origin requests from React frontend
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
        
        // Handle OPTIONS preflight request
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        
        // Delegate to the actual handler
        delegate.handle(exchange);
    }
}