package be.pxl.services;

import be.pxl.services.client.LogboekClient;
import be.pxl.services.domain.Category;
import be.pxl.services.domain.NotificationRequest;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Container
    private static MySQLContainer sqlContainer =
            new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    public void getProduct() throws Exception {
        Product product = Product.builder()
                .name("test")
                .description("test test")
                .price(50.0)
                .category(Category.CLOTHING)
                .sustainable(true)
                .build();

        productRepository.save(product);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<Product> products = Arrays.asList(objectMapper.readValue(jsonResponse, Product[].class));

        assertEquals(1, products.size());
        assertEquals("test", product.getName());
        assertEquals(50.0, product.getPrice());
        assertEquals(Category.CLOTHING, product.getCategory());
        assertTrue(product.isSustainable());
    }

    @Test
    public void createProduct() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("test")
                .description("test description")
                .price(50.0)
                .category(Category.CLOTHING)
                .label("test-label")
                .build();

        String productRequestJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(status().isCreated());

        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals("test", products.get(0).getName());
        assertEquals(50.0, products.get(0).getPrice());
        assertEquals(Category.CLOTHING, products.get(0).getCategory());
        assertEquals("test-label", products.get(0).getLabel());
    }


    @Test
    void UpdateProduct() throws Exception {
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Product")
                .description("Old description")
                .price(40.0)
                .category(Category.CLOTHING)
                .label("OLD_LABEL")
                .sustainable(true)
                .build();

        productRepository.save(existingProduct);

        ProductRequest productRequest = new ProductRequest("Updated Product", "Updated description", 60.0, Category.CLOTHING, "NEW_LABEL", true);

        Product updatedProduct = new Product(
                productId, "Updated Product", "Updated description", 60.0, Category.CLOTHING, "NEW_LABEL", true);


        when(productService.updateProduct(productId, productRequest)).thenReturn(updatedProduct);


        String responseContent = mockMvc.perform(put("/api/product/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product actualProduct = objectMapper.readValue(responseContent, Product.class);

        assertEquals(updatedProduct.getId(), actualProduct.getId());
        assertEquals(updatedProduct.getName(), actualProduct.getName());
        assertEquals(updatedProduct.getDescription(), actualProduct.getDescription());
        assertEquals(updatedProduct.getPrice(), actualProduct.getPrice());
        assertEquals(updatedProduct.getCategory(), actualProduct.getCategory());
        assertEquals(updatedProduct.getLabel(), actualProduct.getLabel());
        assertEquals(updatedProduct.isSustainable(), actualProduct.isSustainable());
    }
}
