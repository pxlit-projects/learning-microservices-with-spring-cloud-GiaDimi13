package be.pxl.services.services;

import be.pxl.services.client.LogboekClient;
import be.pxl.services.domain.NotificationRequest;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final LogboekClient logboekClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products from the repository");
        List<Product> products = productRepository.findAll();

        log.info("Fetched {} products", products.size());
        return products.stream().map(this::mapToProductResponse).toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getWinkelwagenById(Long winkelwagenId) {
        List<Product> products = productRepository.findByWinkelwagenId(winkelwagenId);

        if (products.isEmpty()){
            System.out.println("No products found for winkelwagenId: " + winkelwagenId);
        }

        return products.stream().map(product -> mapToProductResponse(product)).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        log.debug("Mapping product with id: {} to ProductResponse", product.getId());
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .label(product.getLabel())
                .sustainable(product.isSustainable())
                .build();
    }


    @Override
    public void addProduct(ProductRequest productRequest) {
        log.info("Adding a new product with name: {}", productRequest.getName());
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .label(productRequest.getLabel())
                .build();
        productRepository.save(product);
        log.debug("Product saved with id: {}", product.getId());

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message("Product Created")
                .build();
        logboekClient.sendNotification(notificationRequest);

        rabbitTemplate.convertAndSend("myQueue", product);
        log.info("Notification sent for product creation with name: {}", productRequest.getName());
    }

    @Override
    public Product updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setLabel(productRequest.getLabel());

        productRepository.save(product);
        log.debug("Product with id: {} updated successfully", id);

        return product;
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("Deleting product with id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productRepository.delete(product);
        log.debug("Product with id: {} deleted successfully", productId);
    }

    @Override
    public List<ProductResponse> filterProducts(String category, String label) {
        log.info("Get all products");
        List<Product> filteredProducts = productRepository.findAll();

        if (category != null) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getCategory().getName().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (label != null) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getLabel().equalsIgnoreCase(label))
                    .collect(Collectors.toList());
        }

        return filteredProducts.stream().map(product -> mapToProductResponse(product)).toList();
    }

}
