package com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.service;
import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.model.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductService {
    // Trådsäker samling optimerad för webbserver och trådsäkra Streams
    private final List<Product> productList = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // CREATE
    public Product createProduct(Product product) {
        product.setId(idGenerator.getAndIncrement());
        productList.add(product);
        return product;
    }

    // READ ALL
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    // READ ONE
    public Optional<Product> getProductById(Long id) {
        return productList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // UPDATE
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return getProductById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setPrice(updatedProduct.getPrice());
            return existingProduct;
        });
    }

    // DELETE
    public boolean deleteProduct(Long id) {
        return productList.removeIf(p -> p.getId().equals(id));
    }

    // =========================================================================
    // --- STREAMS LOGIK (Del 2) ---
    // =========================================================================

    // • Sök & Filtrera: Kategori
    public List<Product> getProductsByCategory(String category) {
        return productList.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // • Sök & Filtrera: Lågt lagersaldo tröskel
    public List<Product> getProductsWithLowStock(int threshold) {
        return productList.stream()
                .filter(p -> p.getStockQuantity() < threshold)
                .collect(Collectors.toList());
    }

    // • Analys & Aggregering: Totalt lagervärde (kvantitet × pris)
    public double calculateTotalInventoryValue() {
        return productList.stream()
                .mapToDouble(p -> p.getPrice() * p.getStockQuantity())
                .sum();
    }

    // • Analys & Aggregering: Medelpris per kategori
    public Map<String, Double> getAveragePriceByCategory() {
        return productList.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getPrice)
                ));
    }

    // • Sortering: Topp N dyraste produkter
    public List<Product> getTopNExpensiveProducts(int n) {
        return productList.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // • Sortering: Topp N mest populära produkter (sorterat på högst lagersaldo)
    public List<Product> getTopNPopularProducts(int n) {
        return productList.stream()
                .sorted(Comparator.comparingInt(Product::getStockQuantity).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}
