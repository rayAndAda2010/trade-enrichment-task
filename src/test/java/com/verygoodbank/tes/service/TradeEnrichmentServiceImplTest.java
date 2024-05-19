package com.verygoodbank.tes.service;

import com.verygoodbank.tes.model.Product;
import com.verygoodbank.tes.model.Trade;
import com.verygoodbank.tes.repository.ProductRepository;
import com.verygoodbank.tes.service.impl.ProductServiceImpl;
import com.verygoodbank.tes.service.impl.TradeEnrichmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class TradeEnrichmentServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private TradeEnrichmentServiceImpl tradeEnrichmentService;

    @BeforeEach
    public void init() {
        Mockito.doReturn(mockProducts()).when(productRepository).findAll();
        tradeEnrichmentService = new TradeEnrichmentServiceImpl(productService);
    }


    @Test
    void givenTrades_whenIncludesInvalidDate_thenTheRowWillBeDiscarded() {
        productService.preload();
        // The one with incorrect date pattern will not be published
        StepVerifier.create(tradeEnrichmentService.doEnrich(mockTrades(TestScenario.WITH_INVALID_DATE)))
                .expectNext(Trade.builder().tradeDate("20120501").productId(1L).productName("ProductA").currency("EUR").price("30.0").build())
                .expectNext(Trade.builder().tradeDate("20240601").productId(2L).productName("ProductB").currency("EUR").price("20.0").build())
                .verifyComplete();
    }

    @Test
    void givenTrades_whenProductNotFound_thenReplaceWithParticularString() {
        productService.preload();
        StepVerifier.create(tradeEnrichmentService.doEnrich(mockTrades(TestScenario.WITH_NON_EXIST_PRODUCT)))
                .expectNext(Trade.builder().tradeDate("20120501").productId(1L).productName("ProductA").currency("EUR").price("30.0").build())
                .expectNext(Trade.builder().tradeDate("20240601").productId(2L).productName("ProductB").currency("EUR").price("20.0").build())
                .expectNext(Trade.builder().tradeDate("20120501").productId(999999L).productName("Missing Product Name").currency("EUR").price("28.0").build())
                .verifyComplete();
    }

    private enum TestScenario {
        NORMAL,
        WITH_INVALID_DATE,
        WITH_NON_EXIST_PRODUCT
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

    private List<Product> mockProducts() {

        Product p1 = new Product(1L, "ProductA");
        Product p2 = new Product(2L, "ProductB");
        Product p3 = new Product(3L, "ProductC");

        return Stream.of(p1, p2, p3).collect(Collectors.toList());


    }

}
