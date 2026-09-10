package com.EnterpriseWarehouseManagementSystem.Lab3Grupp5;

import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.model.Product;
import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST - Create a product Status: 201 Created
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Retrieve all products Status: 200 OK
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //Retrieve one product by ID Status: 200 OK or 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update a product Status: 200 OK or 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Delete a product Status: 204 No Content or 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // --- STREAMS ENDPOINTS ---

    @GetMapping("/search/category")
    public ResponseEntity<List<Product>> getByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/search/low-stock")
    public ResponseEntity<List<Product>> getLowStock(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(productService.getProductsWithLowStock(threshold));
    }


    // Ändrad till String för att kunna skicka med en beskrivande text framför siffran
    @GetMapping("/analytics/total-value")
    public ResponseEntity<String> getTotalValue() {
        double totalValue = productService.calculateTotalInventoryValue();
        // Returnerar texten tillsammans med värdet formaterat med två decimaler
        return ResponseEntity.ok("Totalt lagervärde: " + String.format("%.2f", totalValue) + " kr");
    }

    @GetMapping("/analytics/average-prices")
    public ResponseEntity<Map<String, Double>> getAveragePrices() {
        return ResponseEntity.ok(productService.getAveragePriceByCategory());
    }

    @GetMapping("/analytics/top-expensive")
    public ResponseEntity<List<Product>> getTopExpensive(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(productService.getTopNExpensiveProducts(limit));
    }

    @GetMapping("/analytics/top-popular")
    public ResponseEntity<List<Product>> getTopPopular(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(productService.getTopNPopularProducts(limit));
    }
}
