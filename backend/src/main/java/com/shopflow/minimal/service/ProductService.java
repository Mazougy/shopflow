package com.shopflow.minimal.service;

import com.shopflow.minimal.dto.product.ProductPageResponse;
import com.shopflow.minimal.dto.product.ProductRequest;
import com.shopflow.minimal.dto.product.ProductResponse;
import com.shopflow.minimal.entity.Category;
import com.shopflow.minimal.entity.Product;
import com.shopflow.minimal.exception.BadRequestException;
import com.shopflow.minimal.exception.ResourceNotFoundException;
import com.shopflow.minimal.mapper.ProductMapper;
import com.shopflow.minimal.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public ProductPageResponse getAll(int page, int size, String search) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException("Invalid pagination parameters");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = (search != null && !search.isBlank())
                ? productRepository.findByActiveTrueAndNameContainingIgnoreCase(search.trim(), pageable)
                : productRepository.findByActiveTrue(pageable);

        return ProductPageResponse.builder()
                .content(productPage.getContent().stream().map(ProductMapper::toResponse).toList())
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(getActiveEntity(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        validatePrices(request);
        Category category = categoryService.getEntity(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .promoPrice(request.getPromoPrice())
                .stock(request.getStock())
                .active(true)
                .category(category)
                .build();

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        validatePrices(request);
        Product product = getActiveEntity(id);
        Category category = categoryService.getEntity(request.getCategoryId());

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPromoPrice(request.getPromoPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public void softDelete(Long id) {
        Product product = getActiveEntity(id);
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getActiveEntity(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void validatePrices(ProductRequest request) {
        if (request.getPromoPrice() != null && request.getPromoPrice().compareTo(request.getPrice()) > 0) {
            throw new BadRequestException("Promo price cannot be greater than price");
        }
    }
}
