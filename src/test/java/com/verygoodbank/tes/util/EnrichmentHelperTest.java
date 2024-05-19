package com.verygoodbank.tes.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.io.InputStream;
import java.util.ArrayList;

public class EnrichmentHelperTest {

    @Test
    void givenValidDate_whenDoingValidation_thenReturnTrue() {
        Assertions.assertTrue(EnrichmentHelper.isValidDate("20240518"));
    }

    @Test
    void givenInValidDate_whenDoingValidation_thenReturnFalse() {
        Assertions.assertFalse(EnrichmentHelper.isValidDate("2024051"));
        Assertions.assertFalse(EnrichmentHelper.isValidDate("20243340"));
        Assertions.assertFalse(EnrichmentHelper.isValidDate("20241102aaa"));
    }

    @Test
    void givenValidCSVInputStream_whenDoingConversion_thenReturnFlux(){
        final InputStream resourceAsStream = getClass().getResourceAsStream("/trade.csv");
        final Flux<String> csvRowFlux = EnrichmentHelper.convertCSV(resourceAsStream)
                .map(csvRow -> String.join(",", csvRow));

        StepVerifier.create(csvRowFlux)
                .expectNext("2016010a,1,EUR,10.0")
                .expectNext("20160101,2,EUR,20.1")
                .expectNext("20160101,3,EUR,30.34")
                .expectNext("20160101,11,EUR,35.34")
                .expectNext("20160101,10,EUR,35.34")
                .verifyComplete();
    }

    @Test
    void givenEmptyCSVInputStream_whenDoingConversion_thenEmptyFluxWillBeReturned(){
        final InputStream resourceAsStream = getClass().getResourceAsStream("/NonExist.csv");
        final Flux<String[]> csvRowFlux = EnrichmentHelper.convertCSV(resourceAsStream);

        StepVerifier.create(csvRowFlux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(list -> {
                    // Assert that the list is empty because no items were emitted
                    Assertions.assertTrue(list.isEmpty(), "The Flux should be empty.");
                })
                .verifyComplete();
    }


}
