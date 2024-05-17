package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.controller.model.Product;
import com.verygoodbank.tes.repository.ProductRepository;
import com.verygoodbank.tes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Mono<Map<Long, Product>> getAllProducts() {
        final Map<Long, Product> idToProducts = productRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity(),
                        (oldValue, newValue) -> {
                            throw new IllegalArgumentException("Duplicate key: " + oldValue);
                        }));
        // In fact, if the datasource support reactive stream natively, we won't need to create Mono like this.
        return Mono.just(idToProducts);

    }

}
