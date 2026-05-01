package com.ecommerce.product;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Smartphone Apple")
                .price(new BigDecimal("999.99"))
                .stock(50)
                .brand("Apple")
                .active(true)
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByActiveTrue(any())).thenReturn(productPage);

        Page<ProductDto.ProductResponse> result = productService.getAllProducts(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("iPhone 15", result.getContent().get(0).getName());
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto.ProductResponse result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_ShouldSaveAndReturn() {
        ProductDto.CreateRequest request = new ProductDto.CreateRequest();
        request.setName("Samsung Galaxy S24");
        request.setPrice(new BigDecimal("799.99"));
        request.setStock(30);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto.ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldSoftDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        productService.deleteProduct(1L);

        assertFalse(product.isActive());
        verify(productRepository).save(product);
    }
}
