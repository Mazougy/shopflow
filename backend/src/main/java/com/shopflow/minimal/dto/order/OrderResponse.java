package com.shopflow.minimal.dto.order;

import com.shopflow.minimal.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal totalTTC;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
