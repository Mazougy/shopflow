package com.shopflow.minimal.dto.cart;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private LocalDateTime updatedAt;
}
