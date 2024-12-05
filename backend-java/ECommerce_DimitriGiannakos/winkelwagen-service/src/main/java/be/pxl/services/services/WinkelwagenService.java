package be.pxl.services.services;

import be.pxl.services.client.ProductClient;
import be.pxl.services.domain.Winkelwagen;
import be.pxl.services.domain.WinkelwagenItem;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.domain.dto.WinkelwagenItemResponse;
import be.pxl.services.domain.dto.WinkelwagenResponse;
import be.pxl.services.repository.WinkelwagenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WinkelwagenService implements IWinkelwagenService{

    private static final Logger log = LoggerFactory.getLogger(WinkelwagenService.class);

    private final WinkelwagenRepository winkelwagenRepository;
    private final ProductClient productClient;

    @Override
    public Winkelwagen createWinkelwagen() {
        Winkelwagen winkelwagen = Winkelwagen.builder().build();
        return winkelwagenRepository.save(winkelwagen);
    }

    @Override
    public WinkelwagenResponse getWinkelwagenById(Long winkelwagenId) {
        log.info("Fetching winkelwagen with ID: {}", winkelwagenId);
        Winkelwagen winkelwagen = winkelwagenRepository.findById(winkelwagenId)
                .orElseThrow(() -> {
                    log.error("Winkelwagen with ID {} not found", winkelwagenId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Winkelwagen not found");
                });

        log.info("Winkelwagen found: {}", winkelwagen);
        return mapToWinkelwagenResponse(winkelwagen);
    }

    private WinkelwagenResponse mapToWinkelwagenResponse(Winkelwagen winkelwagen) {
        List<WinkelwagenItemResponse> items = winkelwagen.getItems().stream()
                .map(item -> WinkelwagenItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getProductPrice() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        double totalPrice = items.stream()
                .mapToDouble(WinkelwagenItemResponse::getSubtotal)
                .sum();

        return WinkelwagenResponse.builder()
                .id(winkelwagen.getId())
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void addProductToWinkelwagen(Long winkelwagenId, Long productId, int quantity) {
        Winkelwagen winkelwagen = winkelwagenRepository.findById(winkelwagenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Winkelwagen not found"));

        ProductResponse product = productClient.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        //product al in winkelwagen
        Optional<WinkelwagenItem> existingItem = winkelwagen.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            WinkelwagenItem newItem = new WinkelwagenItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setProductPrice(product.getPrice());
            newItem.setQuantity(quantity);
            winkelwagen.getItems().add(newItem);
        }

        winkelwagenRepository.save(winkelwagen);
    }

    @Override
    public void removeProductFromWinkelwagen(Long winkelwagenId, Long productId) {
        log.info("Removing product with ID: {} from winkelwagen with ID: {}", productId, winkelwagenId);

        Winkelwagen winkelwagen = winkelwagenRepository.findById(winkelwagenId)
                .orElseThrow(() -> {
                    log.error("Winkelwagen with ID {} not found", winkelwagenId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Winkelwagen not found");
                });

        Optional<WinkelwagenItem> itemToRemove = winkelwagen.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemToRemove.isPresent()) {
            winkelwagen.getItems().remove(itemToRemove.get());
            winkelwagenRepository.save(winkelwagen);
            log.info("Product removed.");
        } else {
            log.warn("Product with ID {} not found in winkelwagen", productId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in winkelwagen");
        }
    }

}

