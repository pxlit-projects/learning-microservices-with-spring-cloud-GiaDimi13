package be.pxl.services.services;

import be.pxl.services.client.LogboekClient;
import be.pxl.services.domain.NotificationRequest;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductDTO;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

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

    private ProductResponse mapToProductResponse(Product product) {
        log.debug("Mapping product with id: {} to ProductResponse", product.getId());
        return ProductResponse.builder()
                .id(product.getId())
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
                .sustainable(productRequest.isSustainable())
                .build();
        productRepository.save(product);
        log.debug("Product saved with id: {}", product.getId());


        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message("Product Created")
                .build();
        logboekClient.sendNotification(notificationRequest);
        log.info("Notification sent for product creation with name: {}", productRequest.getName());

        ProductDTO productDTO = ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory().toString()) // Enum omzetten naar String
                .label(product.getLabel())
                .sustainable(product.isSustainable())
                .build();

        rabbitTemplate.convertAndSend("myQueue", productDTO);
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
        product.setSustainable(productRequest.isSustainable());

        productRepository.save(product);
        log.debug("Product with id: {} updated successfully", id);


        String message = String.format(
                "Product '%s' (ID: %d) updated by user '%s' on %s",
                product.getName(),
                id,
                "Admin",
                LocalDateTime.now()
        );

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message(message)
                .build();

        logboekClient.sendNotification(notificationRequest);
        log.info("Logbook notification sent successfully for product with id: {}", id);
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
}
