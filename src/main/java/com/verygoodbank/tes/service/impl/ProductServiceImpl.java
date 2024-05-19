package com.verygoodbank.tes.service.impl;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.verygoodbank.tes.model.Product;
import com.verygoodbank.tes.repository.ProductRepository;
import com.verygoodbank.tes.service.ProductService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private AsyncLoadingCache<String, Map<Long, Product>> productCache;

    @PostConstruct
    public void preload() {
        productCache = Caffeine.newBuilder()
                .removalListener((k, v, cause) -> log.info("Cache with key: {} has been removed", k))
                // Temporarily configure 6 hours, it should be configurable. (e.g. Spring config server)
                .expireAfterAccess(Duration.ofHours(6))
                .buildAsync(dataLoader());

        log.info("Start to preload product data to cache...");
        productCache.get(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

    }

    private AsyncCacheLoader<String, Map<Long, Product>> dataLoader() {
        return (date, executor) -> getAllProducts().toFuture();
    }

    @Override
    public Mono<Product> getProductById(String date, Long productId) {
        return Mono.fromFuture(productCache.get(date))
                .flatMap(idToProducts -> {
                    final Product product = idToProducts.get(productId);
                    if(product == null) {
                        log.error("Unable to find product per id: {}", productId);
                        return Mono.empty();
                    }
                    return Mono.just(product);
                });
    }


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
