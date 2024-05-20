package com.verygoodbank.tes.service;

import com.verygoodbank.tes.model.Product;
import com.verygoodbank.tes.repository.ProductRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.verygoodbank.tes.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void init() {
        Mockito.doReturn(mockProducts()).when(productRepository).findAll();
    }

    @Test
    void givenProductCacheInit_whenCallingGetProductById_ThenDataReturnFromCache () {
        productService.preload();
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        final Mono<Product> firstCall = productService.getProductById(date, 1L);
        StepVerifier.create(firstCall)
                .expectNext(new Product(1L, "ProductA"))
                .verifyComplete();

        final Mono<Product> secondCall = productService.getProductById(date, 1L);
        StepVerifier.create(secondCall)
                .expectNext(new Product(1L, "ProductA"))
                .verifyComplete();

        final Mono<Product> thirdCall = productService.getProductById(date, 2L);
        StepVerifier.create(thirdCall)
                .expectNext(new Product(2L, "ProductB"))
                .verifyComplete();

        Mockito.verify(productRepository, Mockito.times(1)).findAll();
    }

    private List<Product> mockProducts() {
        Product p1 = new Product(1L, "ProductA");
        Product p2 = new Product(2L, "ProductB");
        Product p3 = new Product(3L, "ProductC");

        return Stream.of(p1, p2, p3).collect(Collectors.toList());
    }

}
