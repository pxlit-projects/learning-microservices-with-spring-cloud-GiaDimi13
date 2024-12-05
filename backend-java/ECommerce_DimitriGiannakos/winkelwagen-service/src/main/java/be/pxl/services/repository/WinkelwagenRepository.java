package be.pxl.services.repository;


import be.pxl.services.domain.Winkelwagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WinkelwagenRepository extends JpaRepository<Winkelwagen, Long> {
}
