package be.pxl.services.repository;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
