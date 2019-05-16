package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.repositories.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void insert(Product product) {
        productRepository.insert(product);
    }

    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }
}
