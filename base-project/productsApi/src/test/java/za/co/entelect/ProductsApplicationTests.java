package za.co.entelect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.entities.pc.ProductEntity;
import za.co.entelect.repository.pc.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest(classes = ProductsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductsApplicationTests {

    private final ProductRepository productRepository;

    @Autowired
    public ProductsApplicationTests(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testViewAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        log.info("List of products: {}", products);
        assertNotNull(products);
    }

    @Test
    void testGetById() {
        Long id = 1L;
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new AssertionError("Product with ID " + id + " not found"));

        log.info("Retrieved product: {}", product);

        assertNotNull(product.getId(), "Product ID is null");
        assertEquals(id, product.getId(), "Product ID mismatch");
        assertNotNull(product.getName(), "Product name is null");
    }

    @Test
    void testAddProduct() {
        ProductEntity newProduct = createProduct("Device Contract", "Testing description", BigDecimal.valueOf(100.45), "123435454");
        log.info("New product to be saved: {}", newProduct);

        ProductEntity savedProduct = productRepository.save(newProduct);
        log.info("Saved product: {}", savedProduct);

        assertNotNull(savedProduct.getId(), "Saved product ID is null");
        assertProductEquals(newProduct, savedProduct, "Saved product does not match the original");
    }

    @Test
    void testUpdateProduct() {
        ProductEntity initialProduct = createProduct("Initial Product", "Initial Description", BigDecimal.valueOf(100.00), "initial-url");
        log.info("Initial product to be saved: {}", initialProduct);

        ProductEntity savedProduct = productRepository.save(initialProduct);
        Long productId = savedProduct.getId();
        log.info("Saved product: {}", savedProduct);

        ProductEntity updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AssertionError("Product with ID " + productId + " not found"));
        log.info("Retrieved product for update: {}", updatedProduct);

        updateProduct(updatedProduct, BigDecimal.valueOf(150.00));

        productRepository.save(updatedProduct);
        log.info("Updated product: {}", updatedProduct);

        ProductEntity retrievedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AssertionError("Updated product with ID " + productId + " not found"));
        log.info("Retrieved updated product: {}", retrievedProduct);

        assertProductEquals(updatedProduct, retrievedProduct, "Updated product does not match the retrieved product");
    }

    @Test
    void testDeleteProduct() {
        ProductEntity product = createProduct("Test Product", "Test Description", BigDecimal.valueOf(50.00), "test-url");
        log.info("New product to be saved and deleted: {}", product);

        ProductEntity savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        log.info("Saved product: {}", savedProduct);

        productRepository.deleteById(productId);
        log.info("Product with ID {} deleted", productId);

        assertFalse(productRepository.existsById(productId), "Product with ID " + productId + " still exists after deletion");
    }

    private ProductEntity createProduct(String name, String description, BigDecimal price, String imageUrl) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        return product;
    }

    private void updateProduct(ProductEntity product, BigDecimal price) {
        product.setName("Updated Product");
        product.setDescription("Updated Description");
        product.setPrice(price);
        product.setImageUrl("updated-url");
    }

    private void assertProductEquals(ProductEntity expected, ProductEntity actual, String message) {
        assertAll("Product properties",
                () -> assertEquals(expected.getName(), actual.getName(), message),
                () -> assertEquals(expected.getDescription(), actual.getDescription(), message),
                () -> assertEquals(expected.getPrice(), actual.getPrice(), message),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl(), message)
        );
    }
}