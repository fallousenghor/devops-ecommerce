package com.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    @Data
    public static class AddItemRequest {
        @NotNull(message = "L'id produit est obligatoire")
        private Long productId;

        @NotNull
        @Min(value = 1, message = "La quantité doit être au moins 1")
        private Integer quantity;
    }

    @Data
    public static class UpdateItemRequest {
        @NotNull
        @Min(value = 1, message = "La quantité doit être au moins 1")
        private Integer quantity;
    }

    @Data
    public static class CartResponse {
        private Long id;
        private List<CartItemInfo> items;
        private BigDecimal total;
        private int totalItems;

        @Data
        public static class CartItemInfo {
            private Long id;
            private Long productId;
            private String productName;
            private String productImageUrl;
            private BigDecimal productPrice;
            private Integer quantity;
            private BigDecimal subtotal;
        }

        public static CartResponse from(com.ecommerce.cart.entity.Cart cart) {
            CartResponse r = new CartResponse();
            r.setId(cart.getId());
            r.setTotal(cart.getTotal());
            r.setTotalItems(cart.getTotalItems());
            r.setItems(cart.getItems().stream().map(item -> {
                CartItemInfo info = new CartItemInfo();
                info.setId(item.getId());
                info.setProductId(item.getProduct().getId());
                info.setProductName(item.getProduct().getName());
                info.setProductImageUrl(item.getProduct().getImageUrl());
                info.setProductPrice(item.getProduct().getPrice());
                info.setQuantity(item.getQuantity());
                info.setSubtotal(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                return info;
            }).toList());
            return r;
        }
    }
}
