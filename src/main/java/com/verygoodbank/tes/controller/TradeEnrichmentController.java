package com.verygoodbank.tes.controller;

import com.verygoodbank.tes.model.Trade;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import com.verygoodbank.tes.util.EnrichmentHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
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
    private final TradeEnrichmentService tradeEnrichmentService;

    @Operation(summary = "Enrich given trade data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrichment process successfully", content =
                    { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DataBuffer> enrich(@RequestPart("file") Flux<DataBuffer> filePart) {

        final Flux<Trade> trades = filePart
                .flatMap(csvChunk -> EnrichmentHelper.convertCSV(csvChunk.asInputStream())
                        .map(Trade::from));

        // Publish CSV header
        final Flux<DataBuffer> header = Flux.just(Trade.builder().build())
                .map(trade -> bufferFactory.wrap(trade.toCsvHeaderBytes()));

        final AtomicReference<LocalDateTime> startTime = new AtomicReference<>();
        final AtomicLong total = new AtomicLong();

        // Publish enriched trade data
        final Flux<DataBuffer> enrichedTrades = tradeEnrichmentService.doEnrich(trades)
                .mapNotNull(trade -> bufferFactory.wrap(trade.toCsvRowBytes()))
                .doOnSubscribe(s -> startTime.set(LocalDateTime.now()))
                .doOnNext(s -> total.incrementAndGet())
                .doOnComplete(() -> log.info("{} trades have been enriched in {} milliseconds.", total.get(),
                        Duration.between(startTime.get(), LocalDateTime.now()).toMillis()));

        // Publish header firstly, then enriched trade data
        return header
                .concatWith(enrichedTrades);
    }

}


