package be.pxl.services.domain.dto;


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
    private List<WinkelwagenItemResponse> items;
    private Double totalPrice;
}
