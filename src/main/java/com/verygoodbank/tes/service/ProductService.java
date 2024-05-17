package com.verygoodbank.tes.service;

import com.verygoodbank.tes.controller.model.Product;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProductService {

    Mono<Map<Long, Product>> getAllProducts();
}
