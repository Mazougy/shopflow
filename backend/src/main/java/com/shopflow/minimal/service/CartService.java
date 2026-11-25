package com.shopflow.minimal.service;

import com.shopflow.minimal.dto.cart.CartItemRequest;
import com.shopflow.minimal.dto.cart.CartResponse;
import com.shopflow.minimal.dto.cart.UpdateCartItemRequest;
import com.shopflow.minimal.entity.Cart;
import com.shopflow.minimal.entity.CartItem;
import com.shopflow.minimal.entity.Product;
import com.shopflow.minimal.entity.User;
import com.shopflow.minimal.exception.BadRequestException;
import com.shopflow.minimal.exception.ResourceNotFoundException;
import com.shopflow.minimal.mapper.CartMapper;
import com.shopflow.minimal.mapper.ProductMapper;
import com.shopflow.minimal.repository.CartItemRepository;
import com.shopflow.minimal.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final BigDecimal SHIPPING_FEE = new BigDecimal("5.00");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartResponse getCurrentCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        Product product = productService.getActiveEntity(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds current stock");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new BadRequestException("Requested quantity exceeds current stock");
            }
            item.setQuantity(newQuantity);
            item.setUnitPrice(ProductMapper.resolveCurrentPrice(product));
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(ProductMapper.resolveCurrentPrice(product))
                    .build();
            cart.getItems().add(item);
        }

        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        User user = userService.getByEmail(email);
        CartItem item = cartItemRepository.findByIdAndCartUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds current stock");
        }

        item.setQuantity(request.getQuantity());
        item.setUnitPrice(ProductMapper.resolveCurrentPrice(item.getProduct()));
        cartItemRepository.save(item);
        return toResponse(cartRepository.save(item.getCart()));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        User user = userService.getByEmail(email);
        CartItem item = cartItemRepository.findByIdAndCartUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart getOrCreateCart(String email) {
        User user = userService.getByEmail(email);
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    public CartResponse toResponse(Cart cart) {
        BigDecimal subTotal = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = cart.getItems().isEmpty() ? BigDecimal.ZERO : SHIPPING_FEE;
        BigDecimal total = subTotal.add(shippingFee);
        return CartMapper.toResponse(cart, subTotal, shippingFee, total);
    }

    public BigDecimal resolveShippingFee(Cart cart) {
        return cart.getItems().isEmpty() ? BigDecimal.ZERO : SHIPPING_FEE;
    }
}
