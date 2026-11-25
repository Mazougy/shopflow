package com.shopflow.minimal.mapper;

import com.shopflow.minimal.dto.category.CategoryResponse;
import com.shopflow.minimal.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
