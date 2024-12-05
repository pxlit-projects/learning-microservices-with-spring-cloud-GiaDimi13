package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinkelwagenItemResponse {

    private Long productId;
    private String productName;
    private Double productPrice;
    private int quantity;
    private Double subtotal;
}
