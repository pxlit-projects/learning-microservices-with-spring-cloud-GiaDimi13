package be.pxl.services;

import be.pxl.services.domain.Winkelwagen;
import be.pxl.services.domain.dto.WinkelwagenResponse;
import be.pxl.services.repository.WinkelwagenRepository;
import be.pxl.services.services.WinkelwagenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class WinkelwagenTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WinkelwagenRepository winkelwagenRepository;

    @MockBean
    private WinkelwagenService winkelwagenService;

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
    void getWinkelwagenById() throws Exception {
        Winkelwagen winkelwagen = Winkelwagen.builder()
                .quantity(1)
                .totalPrice(110.0)
                .build();

        Winkelwagen savedWinkelwagen = winkelwagenRepository.save(winkelwagen);

        WinkelwagenResponse winkelwagenResponse = WinkelwagenResponse.builder()
                .id(savedWinkelwagen.getId())
                .quantity(1)
                .totalPrice(110.0)
                .build();

        when(winkelwagenService.getWinkelwagenById(savedWinkelwagen.getId())).thenReturn(winkelwagenResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/winkelwagen/{winkelwagenId}", savedWinkelwagen.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        WinkelwagenResponse actualResponse = objectMapper.readValue(jsonResponse, WinkelwagenResponse.class);

        assertEquals(winkelwagenResponse.getId(), actualResponse.getId());
        assertEquals(winkelwagenResponse.getQuantity(), actualResponse.getQuantity());
        assertEquals(winkelwagenResponse.getTotalPrice(), actualResponse.getTotalPrice());
    }

    @Test
    void addProductToWinkelwagen() throws Exception {
        Long winkelwagenId = 1L;
        Long productId = 1L;
        int quantity = 2;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/winkelwagen/{winkelwagenId}/producten", winkelwagenId)
                        .param("productId", productId.toString())
                        .param("quantity", String.valueOf(quantity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void removeProductFromWinkelwagen() throws Exception {
        Long winkelwagenId = 1L;
        Long productId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/winkelwagen/{winkelwagenId}/producten/{productId}", winkelwagenId, productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


}
