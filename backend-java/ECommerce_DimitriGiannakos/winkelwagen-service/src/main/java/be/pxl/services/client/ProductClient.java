package be.pxl.services.client;


import be.pxl.services.domain.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productCatalogus-service")
public interface ProductClient
{
    @GetMapping("/api/product/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}
