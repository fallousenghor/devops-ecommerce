package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartDto.CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCart(user));
        return CartDto.CartResponse.from(cart);
    }

    @Transactional
    public CartDto.CartResponse addItem(String email, CartDto.AddItemRequest request) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCart(user));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        if (!product.isActive()) throw new BadRequestException("Ce produit n'est plus disponible");
        if (product.getStock() < request.getQuantity())
            throw new BadRequestException("Stock insuffisant (disponible: " + product.getStock() + ")");

        cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + request.getQuantity()),
                        () -> cart.getItems().add(CartItem.builder()
                                .cart(cart).product(product).quantity(request.getQuantity()).build())
                );

        return CartDto.CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse updateItem(String email, Long itemId, CartDto.UpdateItemRequest request) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable dans le panier"));

        if (item.getProduct().getStock() < request.getQuantity())
            throw new BadRequestException("Stock insuffisant");

        item.setQuantity(request.getQuantity());
        return CartDto.CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse removeItem(String email, Long itemId) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));

        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return CartDto.CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart createCart(User user) {
        return cartRepository.save(Cart.builder().user(user).build());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }
}
