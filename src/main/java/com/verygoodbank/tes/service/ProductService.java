package com.verygoodbank.tes.service;

import com.verygoodbank.tes.model.Product;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProductService {

    Mono<Product> getProductById(final String cobDate, final Long productId);

    Mono<Map<Long, Product>> getAllProducts();
}
