package com.agricultura.service;

import com.agricultura.dto.ProductInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ProductServiceClient {
    
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8084";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ProductServiceClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public ProductInfo getProductById(Long productId, String authToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PRODUCT_SERVICE_URL + "/api/products/" + productId))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ProductInfo.class);
            } else {
                throw new RuntimeException("Producto no encontrado: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener producto: " + e.getMessage(), e);
        }
    }
    
    public boolean updateProductStock(Long productId, Integer newStock, String authToken) {
        try {
            String requestBody = "{\"stock\":" + newStock + "}";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PRODUCT_SERVICE_URL + "/api/products/" + productId + "/stock"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage(), e);
        }
    }
}