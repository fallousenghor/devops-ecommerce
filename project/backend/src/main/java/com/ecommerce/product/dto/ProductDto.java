package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Le nom est obligatoire")
        private String name;

        private String description;

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être positif")
        private BigDecimal price;

        @NotNull(message = "Le stock est obligatoire")
        @Min(value = 0, message = "Le stock ne peut pas être négatif")
        private Integer stock;

        private String imageUrl;
        private String brand;
        private Long categoryId;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String description;
        @DecimalMin(value = "0.01")
        private BigDecimal price;
        @Min(value = 0)
        private Integer stock;
        private String imageUrl;
        private String brand;
        private Long categoryId;
        private Boolean active;
    }

    @Data
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private String brand;
        private boolean active;
        private LocalDateTime createdAt;
        private CategoryInfo category;

        @Data
        public static class CategoryInfo {
            private Long id;
            private String name;
        }

        public static ProductResponse from(com.ecommerce.product.entity.Product p) {
            ProductResponse r = new ProductResponse();
            r.setId(p.getId());
            r.setName(p.getName());
            r.setDescription(p.getDescription());
            r.setPrice(p.getPrice());
            r.setStock(p.getStock());
            r.setImageUrl(p.getImageUrl());
            r.setBrand(p.getBrand());
            r.setActive(p.isActive());
            r.setCreatedAt(p.getCreatedAt());
            if (p.getCategory() != null) {
                CategoryInfo cat = new CategoryInfo();
                cat.setId(p.getCategory().getId());
                cat.setName(p.getCategory().getName());
                r.setCategory(cat);
            }
            return r;
        }
    }
}
