package be.pxl.services.services;

import be.pxl.services.client.ProductClient;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.Winkelwagen;
import be.pxl.services.domain.dto.WinkelwagenResponse;
import be.pxl.services.repository.WinkelwagenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WinkelwagenService implements IWinkelwagenService{

    private static final Logger log = LoggerFactory.getLogger(WinkelwagenService.class);

    private final WinkelwagenRepository winkelwagenRepository;
    private final ProductClient productClient;

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
        return WinkelwagenResponse.builder()
                .products(winkelwagen.getProducts())
                .totalPrice(winkelwagen.getTotalPrice())
                .quantity(winkelwagen.getQuantity())
                .build();
    }

    @Override
    public void addProductToWinkelwagen(Long winkelwagenId, Long productId, int quantity) {
        log.info("Adding product with ID: {} to winkelwagen with ID: {}", productId, winkelwagenId);

        Winkelwagen winkelwagen = winkelwagenRepository.findById(winkelwagenId)
                .orElseThrow(() -> {
                    log.error("Winkelwagen with ID {} not found", winkelwagenId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Winkelwagen not found");
                });

        Product product = productClient.getProductById(productId);

        if (product == null) {
            log.error("Product with ID {} not found", productId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        log.info("Product {} added to winkelwagen. Current quantity: {}", product.getName(), quantity);

        winkelwagen.getProducts().add(product);
        winkelwagen.setQuantity(winkelwagen.getQuantity() + quantity);
        winkelwagen.setTotalPrice(winkelwagen.getTotalPrice() + product.getPrice() * quantity);

        winkelwagenRepository.save(winkelwagen);

        log.info("Winkelwagen updated. Total price: {}, Total quantity: {}", winkelwagen.getTotalPrice(), winkelwagen.getQuantity());
    }

    @Override
    public void removeProductFromWinkelwagen(Long winkelwagenId, Long productId) {
        log.info("Removing product with ID: {} from winkelwagen with ID: {}", productId, winkelwagenId);

        Winkelwagen winkelwagen = winkelwagenRepository.findById(winkelwagenId)
                .orElseThrow(() -> {
                    log.error("Winkelwagen with ID {} not found", winkelwagenId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Winkelwagen not found");
                });

        List<Product> updatedProducts = winkelwagen.getProducts().stream()
                .filter(product -> !product.getId().equals(productId))
                .toList();

        winkelwagen.setProducts(updatedProducts);

        winkelwagen.setQuantity(updatedProducts.size());
        winkelwagen.setTotalPrice(updatedProducts.stream().mapToDouble(Product::getPrice).sum());

        winkelwagenRepository.save(winkelwagen);

        log.info("Product removed. New total price: {}, New quantity: {}", winkelwagen.getTotalPrice(), winkelwagen.getQuantity());
    }
}

