package com.shopflow.minimal.service;

import com.shopflow.minimal.dto.category.CategoryRequest;
import com.shopflow.minimal.dto.category.CategoryResponse;
import com.shopflow.minimal.entity.Category;
import com.shopflow.minimal.exception.BadRequestException;
import com.shopflow.minimal.exception.ResourceNotFoundException;
import com.shopflow.minimal.mapper.CategoryMapper;
import com.shopflow.minimal.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    public Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
