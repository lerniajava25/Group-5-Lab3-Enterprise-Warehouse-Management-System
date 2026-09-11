package com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.service;

import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService service;

    @Mock
    private Product updatedProduct;

    @BeforeEach
    void setUp() {
        // En ny service ger tester utan data fran tidigare test.
        service = new ProductService();
    }

    // skapa, hamta, uppdatera och radera produkter.

    @Test
    void createProductAssignsId() {
        Product first = service.createProduct(product(100L, "Keyboard", "Electronics", 89.99, 10));
        Product second = service.createProduct(product(200L, "Mouse", "Electronics", 29.99, 20));

        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
        assertEquals(List.of(first, second), service.getAllProducts());
    }

    @Test
    void getAllProductsIsEmpty() {
        assertTrue(service.getAllProducts().isEmpty());
    }

    @Test
    void getAllProductsReturnsCopy() {
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        List<Product> products = service.getAllProducts();
        products.clear();

        assertEquals(1, service.getAllProducts().size());
    }

    @Test
    void getProductByIdFindsProduct() {
        Product product = service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        Optional<Product> result = service.getProductById(product.getId());

        assertTrue(result.isPresent());
        assertSame(product, result.orElseThrow());
    }

    @Test
    void getProductByIdReturnsEmpty() {
        assertTrue(service.getProductById(999L).isEmpty());
    }

    @Test
    void updateProductChangesFields() {
        Product existingProduct = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 10));

        // Mockito simulerar produkten som skickas in vid uppdatering.
        when(updatedProduct.getName()).thenReturn("Mouse");
        when(updatedProduct.getPrice()).thenReturn(29.99);

        Optional<Product> result = service.updateProduct(existingProduct.getId(), updatedProduct);

        assertTrue(result.isPresent());
        assertSame(existingProduct, result.orElseThrow());
        assertEquals("Mouse", existingProduct.getName());
        assertEquals(29.99, existingProduct.getPrice());
        verify(updatedProduct).getName();
        verify(updatedProduct).getPrice();
    }

    @Test
    void updateProductReturnsEmpty() {
        assertTrue(service.updateProduct(999L, updatedProduct).isEmpty());

        verifyNoInteractions(updatedProduct);
    }

    @Test
    void deleteProductRemovesProduct() {
        Product product = service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.deleteProduct(product.getId()));
        assertTrue(service.getProductById(product.getId()).isEmpty());
    }

    @Test
    void deleteProductReturnsFalse() {
        assertFalse(service.deleteProduct(999L));
    }

    // Stream-metoder: filtrering, beräkningar och sortering.

    @Test
    void getProductsByCategoryIsCaseInsensitive() {
        Product keyboard = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(2L, "Chair", "Furniture", 49.99, 5));
        service.createProduct(product(3L, "Cable", null, 9.99, 30));

        assertEquals(List.of(keyboard), service.getProductsByCategory("electronics"));
    }

    @Test
    void getProductsByCategoryReturnsEmpty() {
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.getProductsByCategory("Furniture").isEmpty());
    }

    @Test
    void getProductsWithLowStockFindsProducts() {
        Product lowStock = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 4));
        service.createProduct(product(2L, "Mouse", "Electronics", 29.99, 10));

        assertEquals(List.of(lowStock), service.getProductsWithLowStock(5));
    }

    @Test
    void getProductsWithLowStockRespectsThreshold() {
        Product product = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 5));

        assertTrue(service.getProductsWithLowStock(5).isEmpty());
        assertFalse(service.getProductsWithLowStock(6).isEmpty());
        assertSame(product, service.getProductsWithLowStock(6).get(0));
    }

    @Test
    void calculateTotalInventoryValue() {
        service.createProduct(product(1L, "Keyboard", "Electronics", 10.00, 3));
        service.createProduct(product(2L, "Mouse", "Electronics", 5.50, 2));

        assertEquals(41.0, service.calculateTotalInventoryValue());
    }

    @Test
    void calculateTotalInventoryValueIsZeroWhenEmpty() {
        assertEquals(0.0, service.calculateTotalInventoryValue());
    }

    @Test
    void getAveragePriceByCategory() {
        service.createProduct(product(1L, "Keyboard", "Electronics", 90.00, 10));
        service.createProduct(product(2L, "Mouse", "Electronics", 30.00, 10));
        service.createProduct(product(3L, "Chair", null, 50.00, 5));

        Map<String, Double> result = service.getAveragePriceByCategory();

        assertEquals(Map.of("Electronics", 60.0), result);
    }

    @Test
    void getTopNExpensiveProductsSortsByPrice() {
        Product expensive = service.createProduct(
                product(1L, "Monitor", "Electronics", 199.99, 3));
        Product medium = service.createProduct(
                product(2L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(3L, "Mouse", "Electronics", 29.99, 20));

        assertEquals(List.of(expensive, medium), service.getTopNExpensiveProducts(2));
    }

    @Test
    void getTopNPopularProductsSortsByStock() {
        Product popular = service.createProduct(
                product(1L, "Mouse", "Electronics", 29.99, 20));
        Product second = service.createProduct(
                product(2L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(3L, "Monitor", "Electronics", 199.99, 3));

        assertEquals(List.of(popular, second), service.getTopNPopularProducts(2));
    }

    @Test
    void topNMethodsReturnEmptyForZero() {
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.getTopNExpensiveProducts(0).isEmpty());
        assertTrue(service.getTopNPopularProducts(0).isEmpty());
    }

    @Test
    void topNMethodsRejectNegative() {
        assertThrows(IllegalArgumentException.class, () -> service.getTopNExpensiveProducts(-1));
        assertThrows(IllegalArgumentException.class, () -> service.getTopNPopularProducts(-1));
    }

    // Hjälpmetod för att skapa testprodukter
    private Product product(Long id, String name, String category, double price, int stockQuantity) {
        return new Product(id, name, category, price, stockQuantity, LocalDate.of(2026, 1, 1));
    }
}
