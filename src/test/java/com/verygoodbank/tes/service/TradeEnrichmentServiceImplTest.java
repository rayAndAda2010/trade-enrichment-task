package com.verygoodbank.tes.service;

import com.verygoodbank.tes.controller.model.Product;
import com.verygoodbank.tes.controller.model.Trade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class TradeEnrichmentServiceImplTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private TradeEnrichmentService tradeEnrichmentService;


    @Test
    void givenTrades_whenIncludesInvalidDate_thenTheRowWillBeDiscarded() {

        Mockito.doReturn(mockProducts()).when(productService).getAllProducts();

//        tradeEnrichmentService.doEnrich()


    }

    @Test
    void givenTrades_whenProductNotFound_thenReplaceWithParticularString() {

    }

    @Test
    void givenTrades_whenDuplicatedProductExists_thenProcessingWillNotQuit() {

    }

    private enum TestScenario {
        NORMAL,
        WITH_INVALID_DATE,
        WITH_NON_EXIST_PRODUCT,
        WITH_DUPLICATED_PRODUCT
    }

    private Flux<Trade> mockTrades(TestScenario testScenario) {
        final Stream<Trade> normalTestData = Stream.of(Trade.builder().tradeDate("20120501").productId(1L).currency("EUR").price("30.0").build(),
                Trade.builder().tradeDate("20240601").productId(2L).currency("EUR").price("20.0").build());

        switch(testScenario) {
            case NORMAL -> {
                return Flux.fromStream(normalTestData);
            }
            case WITH_INVALID_DATE -> {
                final Stream<Trade> wrongDate = Stream.of(Trade.builder().tradeDate("2024aa01").productId(3L).currency("EUR").price("28.0").build());
                return Flux.fromStream(Stream.concat(normalTestData, wrongDate));
            }
            case WITH_NON_EXIST_PRODUCT -> {
                final Stream<Trade> nonExistProductId = Stream.of(Trade.builder().tradeDate("20120501").productId(999999L).currency("EUR").price("28.0").build());
                return Flux.fromStream(Stream.concat(normalTestData, nonExistProductId));
            }
            default -> throw new IllegalArgumentException("Invalid scenario: " + testScenario.name());
        }
    }

    private Mono<Map<Long, Product>> mockProducts() {

        Product p1 = new Product(1L, "ProductA");
        Product p2 = new Product(2L, "ProductB");
        Product p3 = new Product(3L, "ProductC");

        final Stream<Product> products = Stream.of(p1, p2, p3);

        return Mono.just(products.collect(Collectors.toMap(Product::getProductId, p -> p)));

    }

}
