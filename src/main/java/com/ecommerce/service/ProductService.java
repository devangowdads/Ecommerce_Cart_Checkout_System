package com.ecommerce.service;

import com.ecommerce.dto.request.CreateProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ExceptionConstants;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getDescription(),
                request.getCategory()
        );
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+ id));
        return new ProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponse::new);
    }

    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable).map(ProductResponse::new);
    }

    @Transactional
    public ProductResponse updateStock(Long productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->  new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+ productId));
        product.setStockQuantity(newStock);
        return new ProductResponse(productRepository.save(product));
    }
}