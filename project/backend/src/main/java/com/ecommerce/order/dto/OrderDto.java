package com.ecommerce.order.dto;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class CreateOrderRequest {
        @NotBlank(message = "L'adresse de livraison est obligatoire")
        private String shippingAddress;
        private String notes;
    }

    @Data
    public static class UpdateStatusRequest {
        private Order.OrderStatus status;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private String status;
        private BigDecimal totalAmount;
        private String shippingAddress;
        private String notes;
        private LocalDateTime createdAt;
        private List<OrderItemInfo> items;

        @Data
        public static class OrderItemInfo {
            private Long id;
            private Long productId;
            private String productName;
            private BigDecimal unitPrice;
            private Integer quantity;
            private BigDecimal subtotal;
        }

        public static OrderResponse from(Order order) {
            OrderResponse r = new OrderResponse();
            r.setId(order.getId());
            r.setStatus(order.getStatus().name());
            r.setTotalAmount(order.getTotalAmount());
            r.setShippingAddress(order.getShippingAddress());
            r.setNotes(order.getNotes());
            r.setCreatedAt(order.getCreatedAt());
            r.setItems(order.getItems().stream().map(item -> {
                OrderItemInfo info = new OrderItemInfo();
                info.setId(item.getId());
                info.setProductId(item.getProductId());
                info.setProductName(item.getProductName());
                info.setUnitPrice(item.getUnitPrice());
                info.setQuantity(item.getQuantity());
                info.setSubtotal(item.getSubtotal());
                return info;
            }).toList());
            return r;
        }
    }
}
