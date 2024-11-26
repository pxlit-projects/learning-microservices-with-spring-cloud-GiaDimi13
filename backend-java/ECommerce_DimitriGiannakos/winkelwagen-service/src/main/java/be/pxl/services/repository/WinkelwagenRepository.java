package be.pxl.services.repository;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.Winkelwagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinkelwagenRepository extends JpaRepository<Winkelwagen, Long> {
}
