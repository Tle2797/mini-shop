package com.example.minishop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.minishop.dto.ProductRequest;
import com.example.minishop.exception.NotFoundException;
import com.example.minishop.model.Product;
import com.example.minishop.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll(String keyword) {
        return productRepository.findAll(keyword);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> notFound(id));
    }

    public Product create(ProductRequest req) {
        return productRepository.create(req);
    }

    public Product update(Long id, ProductRequest req) {
        return productRepository.update(id, req)
                .orElseThrow(() -> notFound(id));
    }

    public void delete(Long id) {
        if (!productRepository.deleteById(id)) {
            throw notFound(id);
        }
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("ไม่พบสินค้า id = " + id);
    }
}