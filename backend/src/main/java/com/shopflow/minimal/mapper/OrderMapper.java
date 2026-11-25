package com.shopflow.minimal.mapper;

import com.shopflow.minimal.dto.order.OrderItemResponse;
import com.shopflow.minimal.dto.order.OrderResponse;
import com.shopflow.minimal.entity.Order;
import com.shopflow.minimal.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .subTotal(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .totalTTC(order.getTotalTTC())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(lineTotal)
                .build();
    }
}
