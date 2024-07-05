package za.co.entelect.controllers;

import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.entelect.pojo.pc.DetailedProduct;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.services.pc.ProductService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            log.info("List of products: " + products);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (NoResultException e) {
            log.warn("No products found: " + e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping( "/paginated")
    public ResponseEntity<List<Product>> getProductsInPages(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size){
        List<Product> products = productService.getPaginatedProducts(page, size);
        log.info("List of paginated products {} for page {} of size {} ", products, page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<DetailedProduct> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            DetailedProduct detailedProduct = new DetailedProduct(product);
            productService.setEligibilityCriteria(detailedProduct);
            log.info("Product: " + detailedProduct);
            return ResponseEntity.ok(detailedProduct);
        } else {
            log.warn("Product with id {} not found ", productId);
            return ResponseEntity.notFound().build();
        }
    }
}