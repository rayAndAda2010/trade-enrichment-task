package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.model.Product;
import com.verygoodbank.tes.model.Trade;
import com.verygoodbank.tes.service.ProductService;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import com.verygoodbank.tes.util.EnrichmentHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {

    private final ProductService productService;

    @Override
    public Flux<Trade> doEnrich(final Flux<Trade> incomingTrades) {
        return incomingTrades
                .filter(trade -> {
                    if (EnrichmentHelper.isValidDate(trade.getTradeDate())) return true;
                    log.warn("Invalid date: {}", trade.getTradeDate());
                    return false;
                })
                .flatMap(trade -> {
                    final Long productId = trade.getProductId();
                    return productService.getProductById(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), productId)
                            .switchIfEmpty(Mono.just(new Product(-1L, "Missing Product Name")))
                            .map(product -> {
                                trade.setProductName(product.getProductName());
                                return trade;
                            });
                }).onErrorResume(Exception.class, e -> {
                            log.error(e.getMessage());
                            return Mono.empty();
                        }
                ).publishOn(Schedulers.boundedElastic());
    }
}
