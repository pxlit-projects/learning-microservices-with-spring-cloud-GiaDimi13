package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private Long id;

    private Long winkelwagenId;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String label; // Labels for additional categorization or tags
    private boolean Sustainable;

}
