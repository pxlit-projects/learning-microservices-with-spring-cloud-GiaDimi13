package be.pxl.services.controller;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final IProductService productService;

    // Producten opvragen (alle producten)
    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getProducts() {
        log.info("Fetching all products");
        List<ProductResponse> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Product toevoegen
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductRequest productRequest) {
        log.info("Adding a new product: {}", productRequest.getName());
        productService.addProduct(productRequest);
    }

    // Product opvragen op basis van ID (voor bewerken)
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponse productResponse = productService.getProductById(id);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // Product bewerken
    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        log.info("Updating product with ID: {}", productId);
        Product updatedProduct = productService.updateProduct(productId, productRequest);
        log.info("Product with ID {} updated successfully", productId);
        return ResponseEntity.ok(updatedProduct);
    }

    // Product verwijderen (optioneel, als beheerder de mogelijkheid heeft om producten te verwijderen)
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product with ID: {}", productId);
        productService.deleteProduct(productId);
        log.info("Product with ID {} deleted successfully", productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String label) {
        List<ProductResponse> products = productService.filterProducts(category, label);
        return ResponseEntity.ok(products);
    }
}
