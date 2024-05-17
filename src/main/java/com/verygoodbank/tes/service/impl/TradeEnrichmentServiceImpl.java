package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.controller.model.Product;
import com.verygoodbank.tes.controller.model.Trade;
import com.verygoodbank.tes.service.ProductService;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import com.verygoodbank.tes.util.EnrichmentHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.function.TupleUtils;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {

    private final ProductService productService;

    @Override
    public Flux<Trade> doEnrich(final Flux<Trade> trades) {
        // Get & cache product mapping mono
        Mono<Map<Long, Product>> productMappingMono = productService.getAllProducts().cache();

        return trades
                .filter(trade -> EnrichmentHelper.isValidDate(trade.getTradeDate()))
                .zipWith(productMappingMono.repeat())
                .map(TupleUtils.function((trade, productMapping) -> {
                    final Long productId = trade.getProductId();
                    final Product product = productMapping.get(productId);

                    final String productName = Optional.ofNullable(product)
                            .map(prd -> {
                                if ("".equals(prd.getProductName())) return null;
                                return prd.getProductName();
                            }).orElseGet(() -> {
                                log.warn("Missing product mapping for the trade: {}", trade);
                                return "Missing Product Name";
                            });
                    trade.setProductName(productName);
                    return trade;
                }))
                .onErrorResume(Exception.class, e -> {
                        log.error(e.getMessage(), e);
                        return Mono.empty();
                    }
                ).publishOn(Schedulers.boundedElastic());
    }
}
