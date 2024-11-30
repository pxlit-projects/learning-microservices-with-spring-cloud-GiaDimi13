package be.pxl.services.repository;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByWinkelwagenId(Long winkelwagenId);

}
