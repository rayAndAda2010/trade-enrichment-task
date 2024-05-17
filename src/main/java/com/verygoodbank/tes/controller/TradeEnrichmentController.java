package com.verygoodbank.tes.controller;

import com.verygoodbank.tes.controller.model.Trade;
import com.verygoodbank.tes.service.impl.TradeEnrichmentServiceImpl;
import com.verygoodbank.tes.util.EnrichmentHelper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class TradeEnrichmentController {

    private final DataBufferFactory bufferFactory;
    private final TradeEnrichmentServiceImpl tradeEnrichmentServiceImpl;

    //TODO: Add @ApiResponse for different responseCode
    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enrich given trade data")
    public Flux<DataBuffer> enrich(@RequestPart("file") Flux<DataBuffer> filePart) {

        final Flux<Trade> trades = filePart
                .flatMap(csvChunk -> EnrichmentHelper.convertCSV(csvChunk.asInputStream())
                        .map(Trade::from));

        final AtomicReference<LocalDateTime> startTime = new AtomicReference<>();
        final AtomicLong total = new AtomicLong();

        return tradeEnrichmentServiceImpl.doEnrich(trades)
                .mapNotNull(trade -> bufferFactory.wrap(trade.toCSVBytes()))
                .doOnSubscribe(s -> startTime.set(LocalDateTime.now()))
                .doOnNext(s -> total.incrementAndGet())
                .doOnComplete(() -> log.info("{} trades have been enriched in {} milliseconds.", total.get(), Duration.between(startTime.get(), LocalDateTime.now()).toMillis()));

    }

}


