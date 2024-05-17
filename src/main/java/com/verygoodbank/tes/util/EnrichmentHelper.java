package com.verygoodbank.tes.util;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// TODO is it good to do logging in util class
@Slf4j
public class EnrichmentHelper {

    private static final DateTimeFormatter yyyyMMddPattern = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static boolean isValidDate(final String tradeDate) {
        if (tradeDate == null || "".equals(tradeDate))
            return false;
        try {
            LocalDate.parse(tradeDate, yyyyMMddPattern);
        } catch (DateTimeParseException e) {
            log.warn("Invalid date: {}", tradeDate);
            return false;
        }
        return true;
    }

    public static Flux<String[]> convertCSV(final InputStream ins) {
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
