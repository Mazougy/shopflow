package com.shopflow.minimal.mapper;

import com.shopflow.minimal.dto.product.ProductResponse;
import com.shopflow.minimal.entity.Product;

import java.math.BigDecimal;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .promoPrice(product.getPromoPrice())
                .currentPrice(resolveCurrentPrice(product))
                .stock(product.getStock())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public static BigDecimal resolveCurrentPrice(Product product) {
        return product.getPromoPrice() != null ? product.getPromoPrice() : product.getPrice();
    }
}
