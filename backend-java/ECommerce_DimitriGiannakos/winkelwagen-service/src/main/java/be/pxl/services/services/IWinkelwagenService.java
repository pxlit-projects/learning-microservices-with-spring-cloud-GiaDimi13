package be.pxl.services.services;

import be.pxl.services.domain.Winkelwagen;
import be.pxl.services.domain.dto.WinkelwagenResponse;

public interface IWinkelwagenService {

    void addProductToWinkelwagen(Long winkelwagenId, Long productId, int quantity);

    WinkelwagenResponse getWinkelwagenById(Long winkelwagenId);

    void removeProductFromWinkelwagen(Long winkelwagenId, Long productId);

    Winkelwagen createWinkelwagen();
}
