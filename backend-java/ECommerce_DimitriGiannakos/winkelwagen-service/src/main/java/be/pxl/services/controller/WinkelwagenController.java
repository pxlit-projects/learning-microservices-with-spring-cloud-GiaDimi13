package be.pxl.services.controller;

import be.pxl.services.domain.Winkelwagen;
import be.pxl.services.domain.dto.WinkelwagenResponse;
import be.pxl.services.services.WinkelwagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/winkelwagen")
@RequiredArgsConstructor
public class WinkelwagenController {

    private final WinkelwagenService winkelwagenService;


    @PostMapping
    public ResponseEntity<Winkelwagen> createWinkelwagen() {
        Winkelwagen winkelwagen = winkelwagenService.createWinkelwagen();
        return ResponseEntity.status(HttpStatus.CREATED).body(winkelwagen);
    }
    @GetMapping("/{winkelwagenId}")
    public ResponseEntity<WinkelwagenResponse> getWinkelwagenById(@PathVariable Long winkelwagenId) {
        return new ResponseEntity<>(winkelwagenService.getWinkelwagenById(winkelwagenId), HttpStatus.OK);
    }

    @PostMapping("/{winkelwagenId}/producten")
    public ResponseEntity<Void> addProductToWinkelwagen(
            @PathVariable Long winkelwagenId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        winkelwagenService.addProductToWinkelwagen(winkelwagenId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{winkelwagenId}/producten/{productId}")
    public ResponseEntity<Void> removeProductFromWinkelwagen(
            @PathVariable Long winkelwagenId,
            @PathVariable Long productId) {
        winkelwagenService.removeProductFromWinkelwagen(winkelwagenId, productId);
        return ResponseEntity.noContent().build();
    }
}
