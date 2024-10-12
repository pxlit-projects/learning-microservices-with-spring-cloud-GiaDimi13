package be.pxl.services.domain;

import be.pxl.services.domain.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "winkelwagen")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Winkelwagen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //private Long customerId; // Referentie naar de klant
    @ElementCollection
    private List<ProductDTO> items; // Lijst van producten in de winkelwagen

    private double totalPrice; // Totaalprijs van de winkelwagen
}
