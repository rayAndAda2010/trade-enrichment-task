package com.verygoodbank.tes.service;

import com.verygoodbank.tes.controller.model.Trade;
import reactor.core.publisher.Flux;

public interface TradeEnrichmentService {
    Flux<Trade> doEnrich(final Flux<Trade> trades);
}
