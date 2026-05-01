package com.ecommerce.common;

import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCategories();
        seedAdminUser();
        seedProducts();
        log.info("✅ Données initiales chargées avec succès");
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                Category.builder().name("Smartphones").description("Téléphones intelligents").build(),
                Category.builder().name("Ordinateurs").description("PC portables et de bureau").build(),
                Category.builder().name("Tablettes").description("Tablettes tactiles").build(),
                Category.builder().name("Accessoires").description("Câbles, coques, chargeurs").build(),
                Category.builder().name("Audio").description("Casques, écouteurs, enceintes").build(),
                Category.builder().name("TV & Écrans").description("Téléviseurs et moniteurs").build()
            );
            categoryRepository.saveAll(categories);
            log.info("📦 {} catégories créées", categories.size());
        }
    }

    private void seedAdminUser() {
        if (!userRepository.existsByEmail("admin@electronics.com")) {
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@electronics.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("👤 Admin créé : admin@electronics.com / Admin@123");
        }

        if (!userRepository.existsByEmail("user@electronics.com")) {
            User user = User.builder()
                    .firstName("Jean")
                    .lastName("Dupont")
                    .email("user@electronics.com")
                    .password(passwordEncoder.encode("User@123"))
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user);
            log.info("👤 User test créé : user@electronics.com / User@123");
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            Category smartphones = categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals("Smartphones")).findFirst().orElse(null);
            Category ordinateurs = categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals("Ordinateurs")).findFirst().orElse(null);
            Category audio = categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals("Audio")).findFirst().orElse(null);
            Category tablettes = categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals("Tablettes")).findFirst().orElse(null);

            List<Product> products = List.of(
                Product.builder()
                    .name("iPhone 15 Pro")
                    .description("Le dernier iPhone avec puce A17 Pro, écran Super Retina XDR 6.1 pouces")
                    .price(new BigDecimal("1299.99"))
                    .stock(50)
                    .brand("Apple")
                    .imageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400")
                    .category(smartphones)
                    .build(),
                Product.builder()
                    .name("Samsung Galaxy S24 Ultra")
                    .description("Smartphone haut de gamme avec S Pen intégré, 200MP, écran 6.8 pouces")
                    .price(new BigDecimal("1399.99"))
                    .stock(35)
                    .brand("Samsung")
                    .imageUrl("https://images.unsplash.com/photo-1707052788524-7e7de5bc1d64?w=400")
                    .category(smartphones)
                    .build(),
                Product.builder()
                    .name("MacBook Pro M3")
                    .description("Ordinateur portable Apple avec puce M3, 16GB RAM, SSD 512GB")
                    .price(new BigDecimal("2499.99"))
                    .stock(20)
                    .brand("Apple")
                    .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400")
                    .category(ordinateurs)
                    .build(),
                Product.builder()
                    .name("Dell XPS 15")
                    .description("PC portable premium Intel Core i9, RTX 4060, écran OLED 4K")
                    .price(new BigDecimal("1899.99"))
                    .stock(15)
                    .brand("Dell")
                    .imageUrl("https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400")
                    .category(ordinateurs)
                    .build(),
                Product.builder()
                    .name("Sony WH-1000XM5")
                    .description("Casque Bluetooth à réduction de bruit active, 30h d'autonomie")
                    .price(new BigDecimal("349.99"))
                    .stock(80)
                    .brand("Sony")
                    .imageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400")
                    .category(audio)
                    .build(),
                Product.builder()
                    .name("AirPods Pro 2")
                    .description("Écouteurs sans fil Apple avec ANC adaptatif et audio spatial")
                    .price(new BigDecimal("279.99"))
                    .stock(100)
                    .brand("Apple")
                    .imageUrl("https://images.unsplash.com/photo-1588423771073-b8903fead714?w=400")
                    .category(audio)
                    .build(),
                Product.builder()
                    .name("iPad Pro M2 12.9\"")
                    .description("Tablette Apple avec puce M2, écran Liquid Retina XDR, 256GB")
                    .price(new BigDecimal("1199.99"))
                    .stock(25)
                    .brand("Apple")
                    .imageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400")
                    .category(tablettes)
                    .build(),
                Product.builder()
                    .name("Samsung Galaxy Tab S9")
                    .description("Tablette Android premium, écran AMOLED 11 pouces, S Pen inclus")
                    .price(new BigDecimal("899.99"))
                    .stock(30)
                    .brand("Samsung")
                    .imageUrl("https://images.unsplash.com/photo-1589739900266-43b2843f4c12?w=400")
                    .category(tablettes)
                    .build()
            );

            productRepository.saveAll(products);
            log.info("🛍️ {} produits créés", products.size());
        }
    }
}
