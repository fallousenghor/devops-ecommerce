package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Panier", description = "Gestion du panier d'achats")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Voir son panier")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getUsername())));
    }

    @PostMapping("/items")
    @Operation(summary = "Ajouter un produit au panier")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> addItem(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CartDto.AddItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Produit ajouté",
                cartService.addItem(user.getUsername(), request)));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Modifier la quantité d'un article")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId,
            @Valid @RequestBody CartDto.UpdateItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Quantité mise à jour",
                cartService.updateItem(user.getUsername(), itemId, request)));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Supprimer un article du panier")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Article supprimé",
                cartService.removeItem(user.getUsername(), itemId)));
    }

    @DeleteMapping
    @Operation(summary = "Vider le panier")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Panier vidé", null));
    }
}
