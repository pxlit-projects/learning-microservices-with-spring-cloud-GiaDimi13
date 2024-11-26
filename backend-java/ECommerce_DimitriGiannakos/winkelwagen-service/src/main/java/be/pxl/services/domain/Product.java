package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    private Long winkelwagenId;
    private String name;
    private String description;
    private Double price;
    private Category category;
    private String label;
    private boolean sustainable;
}
