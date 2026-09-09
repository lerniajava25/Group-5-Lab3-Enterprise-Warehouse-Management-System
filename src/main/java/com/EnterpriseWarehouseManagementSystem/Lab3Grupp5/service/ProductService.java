package com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.service;

import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    // Thread-safe list and ID generator
    private final List<Product> productList = Collections.synchronizedList(new ArrayList<>());
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
}
