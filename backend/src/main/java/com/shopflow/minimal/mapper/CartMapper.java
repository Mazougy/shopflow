package com.shopflow.minimal.mapper;

import com.shopflow.minimal.dto.cart.CartItemResponse;
import com.shopflow.minimal.dto.cart.CartResponse;
import com.shopflow.minimal.entity.Cart;
import com.shopflow.minimal.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart, BigDecimal subTotal, BigDecimal shippingFee, BigDecimal total) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartMapper::toItemResponse)
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .total(total)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(lineTotal)
                .build();
    }
}
