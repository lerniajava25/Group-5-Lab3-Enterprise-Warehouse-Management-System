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
/*
 * Den har testklassen testar ProductService utan databas eller Spring-context.
 * Varje test kontrollerar ett beteende i servicen med JUnit 5.
 * Mockito anvands bara for att simulera en produkt vid uppdatering.
 */
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
        // Happy path: nya produkter ska fa ID:n i ordningen 1, 2, 3 ...
        Product first = service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));
        Product second = service.createProduct(product(2L, "Mouse", "Electronics", 29.99, 20));

        // Kontrollera bade ID:n och att bada produkterna sparades.
        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
        assertEquals(List.of(first, second), service.getAllProducts());
    }

    @Test
    void getAllProductsIsEmpty() {
        // Edge case: en ny service ska inte innehalla nagra produkter.
        assertTrue(service.getAllProducts().isEmpty());
    }

    @Test
    void getAllProductsReturnsCopy() {
        // Den returnerade listan ska vara en kopia av service-listan.
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        List<Product> products = service.getAllProducts();
        products.clear();

        assertEquals(1, service.getAllProducts().size());
    }

    @Test
    void getProductByIdFindsProduct() {
        // Happy path: hamta en produkt som finns.
        Product product = service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        Optional<Product> result = service.getProductById(product.getId());

        assertTrue(result.isPresent());
        assertSame(product, result.orElseThrow());
    }

    @Test
    void getProductByIdReturnsEmpty() {
        // Edge case: ett okant ID ska ge Optional.empty().
        assertTrue(service.getProductById(999L).isEmpty());
    }

    @Test
    void updateProductChangesFields() {
        // Happy path: uppdatera namn och pris pa en befintlig produkt.
        Product existingProduct = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 10));

        // Mockito simulerar den nya produkten som skickas in vid uppdatering.
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
        // Edge case: om ID:t saknas ska inget uppdateras.
        assertTrue(service.updateProduct(999L, updatedProduct).isEmpty());

        // Uppdateringsobjektet ska inte anvandas nar produkten saknas.
        verifyNoInteractions(updatedProduct);
    }

    @Test
    void deleteProductRemovesProduct() {
        // Happy path: en befintlig produkt ska kunna raderas.
        Product product = service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.deleteProduct(product.getId()));
        assertTrue(service.getProductById(product.getId()).isEmpty());
    }

    @Test
    void deleteProductReturnsFalse() {
        // Edge case: radering av ett okant ID ska returnera false.
        assertFalse(service.deleteProduct(999L));
    }

    // Stream-metoder: filtrering, beräkningar och sortering.

    @Test
    void getProductsByCategoryIsCaseInsensitive() {
        // Sökningen ska fungera med både stora och små bokstäver.
        Product keyboard = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(2L, "Chair", "Furniture", 49.99, 5));
        service.createProduct(product(3L, "Cable", null, 9.99, 30));

        assertEquals(List.of(keyboard), service.getProductsByCategory("electronics"));
    }

    @Test
    void getProductsByCategoryReturnsEmpty() {
        // Om ingen produkt matchar kategorin ska resultatet vara tomt.
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.getProductsByCategory("Furniture").isEmpty());
    }

    @Test
    void getProductsWithLowStockFindsProducts() {
       // Produkter som har mindre lager än gränsen ska hittas.        Product lowStock = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 4));
        service.createProduct(product(2L, "Mouse", "Electronics", 29.99, 10));

        assertEquals(List.of(lowStock), service.getProductsWithLowStock(5));
    }

    @Test
    void getProductsWithLowStockRespectsThreshold() {
        // En produkt på exakt gränsen ska inte räknas som låg i lager.
        Product product = service.createProduct(
                product(1L, "Keyboard", "Electronics", 89.99, 5));

        assertTrue(service.getProductsWithLowStock(5).isEmpty());
        assertFalse(service.getProductsWithLowStock(6).isEmpty());
        assertSame(product, service.getProductsWithLowStock(6).get(0));
    }

    @Test
    void calculateTotalInventoryValue() {
        // Lagervärde beräknas som pris multiplicerat med lagersaldo.
        service.createProduct(product(1L, "Keyboard", "Electronics", 10.00, 3));
        service.createProduct(product(2L, "Mouse", "Electronics", 5.50, 2));

        assertEquals(41.0, service.calculateTotalInventoryValue());
    }

    @Test
    void calculateTotalInventoryValueIsZeroWhenEmpty() {
        // Ett tomt lager ska ha lagervärdet noll.
        assertEquals(0.0, service.calculateTotalInventoryValue());
    }

    @Test
    void getAveragePriceByCategory() {
        // Genomsnittspriset beräknas separat för varje kategori.
        service.createProduct(product(1L, "Keyboard", "Electronics", 90.00, 10));
        service.createProduct(product(2L, "Mouse", "Electronics", 30.00, 10));
        service.createProduct(product(3L, "Chair", "Furniture", 50.00, 5));
        service.createProduct(product(4L, "Table", "Furniture", 35, 5));


        Map<String, Double> result = service.getAveragePriceByCategory();

        assertEquals(Map.of("Electronics", 60.0, "Furniture", 42.5), result);
    }

    @Test
    void getTopNExpensiveProductsSortsByPrice() {
        // Produkterna ska sorteras från dyrast till billigast.
        Product expensive = service.createProduct(
                product(1L, "Monitor", "Electronics", 199.99, 3));
        Product medium = service.createProduct(
                product(2L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(3L, "Mouse", "Electronics", 29.99, 20));

        assertEquals(List.of(expensive, medium), service.getTopNExpensiveProducts(2));
    }

    @Test
    void getTopNPopularProductsSortsByStock() {
        // De 2 produkterna med flest i lager ska komma först..
        Product popular = service.createProduct(
                product(1L, "Mouse", "Electronics", 29.99, 20));
        Product second = service.createProduct(
                product(2L, "Keyboard", "Electronics", 89.99, 10));
        service.createProduct(product(3L, "Monitor", "Electronics", 199.99, 3));

        assertEquals(List.of(popular, second), service.getTopNPopularProducts(2));
    }

    @Test
    void topNMethodsReturnEmptyForZero() {
        // Edge case: noll produkter ska ge en tom lista.
        service.createProduct(product(1L, "Keyboard", "Electronics", 89.99, 10));

        assertTrue(service.getTopNExpensiveProducts(0).isEmpty());
        assertTrue(service.getTopNPopularProducts(0).isEmpty());
    }

    @Test
    void topNMethodsRejectNegative() {
        // Felhantering: ett negativt antal produkter är ogiltigt.
        assertThrows(IllegalArgumentException.class, () -> service.getTopNExpensiveProducts(-1));
        assertThrows(IllegalArgumentException.class, () -> service.getTopNPopularProducts(-1));
    }

    // Hjälpmetod för att skapa testprodukter
    private Product product(Long id, String name, String category, double price, int stockQuantity) {
        return new Product(id, name, category, price, stockQuantity, LocalDate.of(2026, 1, 1));
    }
}
