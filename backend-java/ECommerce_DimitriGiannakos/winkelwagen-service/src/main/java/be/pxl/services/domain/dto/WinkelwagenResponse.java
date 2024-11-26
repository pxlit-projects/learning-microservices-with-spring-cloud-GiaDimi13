package be.pxl.services.domain.dto;

import be.pxl.services.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinkelwagenResponse {

    private Long id;
    private List<Product> products;
    private int quantity;
    private double totalPrice; // Totaalprijs van de winkelwagen, misschien in frontEnd berekenen

}
