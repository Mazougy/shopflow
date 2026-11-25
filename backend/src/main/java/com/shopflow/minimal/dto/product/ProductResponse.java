package com.shopflow.minimal.dto.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal promoPrice;
    private BigDecimal currentPrice;
    private Integer stock;
    private Boolean active;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
}
