package be.pxl.services.services;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;

import java.util.List;

public interface IProductService {
    void addProduct(ProductRequest productRequest);

    Product updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long productId);

    List<ProductResponse> getAllProducts();
/*
    List<ProductResponse> searchProducts(String query);

    List<ProductResponse> filterProducts(String category, Double price, Boolean isSustainable);

 */

}
