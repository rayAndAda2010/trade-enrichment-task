package com.verygoodbank.tes.util;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class EnrichmentHelper {

    private static final DateTimeFormatter yyyyMMddPattern = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static boolean isValidDate(final String tradeDate) {
        if (Strings.isEmpty(tradeDate)) return false;
        try {
            LocalDate.parse(tradeDate, yyyyMMddPattern);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static Flux<String[]> convertCSV(final InputStream ins) {
        if (ins == null) return Flux.empty();
        try (InputStreamReader insReader = new InputStreamReader(ins);
             BufferedReader bufferedReader = new BufferedReader(insReader);
             CSVReader csvReader = new CSVReader(bufferedReader)) {

            Spliterator<String[]> splitItr = Spliterators
                    .spliteratorUnknownSize(csvReader.iterator(), Spliterator.ORDERED);

            final Stream<String[]> stream = StreamSupport.stream(splitItr, false);
            return Flux.fromIterable(stream
                    .skip(1) // Skip the column head
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
