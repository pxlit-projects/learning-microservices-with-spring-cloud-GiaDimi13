package be.pxl.services.domain.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;
    private String description;
    private Double price;
    private String category;
    private String label; // Labels for additional categorization or tags
    private boolean Sustainable;
}
