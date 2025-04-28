package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Cart;
import com.project.WebTapGym.models.CartItem;
import com.project.WebTapGym.models.Product;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.CartItemRepository;
import com.project.WebTapGym.repositories.CartRepository;
import com.project.WebTapGym.repositories.ProductRepository;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.CartResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Override
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        if(quantity <= 0 )
        {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(existingUser)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }


        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if(existingItem.isPresent())
        {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(existingProduct)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        return CartResponse.fromCart(cart);
    }

    @Override
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) { // update số lượng
        if(quantity <= 0 ){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartRepository.save(cart);
    }

    @Override
    public void removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getCartItems().clear();
        cartItemRepository.deleteAll();
        cartRepository.save(cart);

    }
}
