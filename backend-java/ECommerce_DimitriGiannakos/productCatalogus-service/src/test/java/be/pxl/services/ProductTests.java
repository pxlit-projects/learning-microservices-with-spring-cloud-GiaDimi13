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
import org.junit.jupiter.api.Test;
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

    @MockBean
    private LogboekClient logboekClient;


    @Container
    private static MySQLContainer sqlContainer =
            new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    public void getProduct() throws Exception {

        // Maak een Product-object
        Product product = Product.builder()
                .name("test")
                .description("test test")
                .price(50.0)
                .winkelwagenId(1L)
                .category(Category.CLOTHING)
                .sustainable(true)
                .build();

        productRepository.save(product);

        // Voer de GET-aanroep uit
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Haal de JSON-respons op
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<Product> products = Arrays.asList(objectMapper.readValue(jsonResponse, Product[].class));

        // Assertions voor producten
        assertEquals(1, products.size());
        assertEquals("test", product.getName());
        assertEquals(50.0, product.getPrice());
        assertEquals(Category.CLOTHING, product.getCategory());
        assertTrue(product.isSustainable());
    }

    @Test
    public void createProduct() throws Exception {
        // Create a sample product request
        Product product = Product.builder()
                .id(1L)
                .winkelwagenId(1L)
                .name("test")
                .description("test description")
                .price(50.0)
                .category(Category.CLOTHING)
                .label("test-label")
                .build();

        // Convert the request to JSON
        String productRequestJson = objectMapper.writeValueAsString(product);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(status().isCreated());


        // Assertions to verify the response
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals("test", products.get(0).getName());
        assertEquals(50.0, products.get(0).getPrice());
        assertEquals(Category.CLOTHING, products.get(0).getCategory());
        assertEquals("test-label", products.get(0).getLabel());

    }
/*
    @Test
    void testUpdateProduct() throws Exception {
        // Arrange: create a sample product and a ProductRequest
        Long productId = 1L;
        Product product = new Product(productId,1L,"Product", "description", 60.0,Category.CLOTHING ,"NEW_LABEL",true);
        Product updatedProduct = new Product(productId,1L,"Updated Product", "Updated description", 50.0,Category.CLOTHING ,"NEW_updated_LABEL",true);

        // Mock the service layer behavior
        when(productService.updateProduct(productId, productRequest)).thenReturn(updatedProduct);

        // Act & Assert: perform PUT request and verify status and response body
        mockMvc.perform(put("/api/product/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(60.0))
                .andExpect(jsonPath("$.category").value("CLOTHING"))
                .andExpect(jsonPath("$.label").value("NEW_LABEL"));

        // Verify that the productService.updateProduct was called with correct parameters
        verify(productService, times(1)).updateProduct(productId, productRequest);
    }

 */

}
