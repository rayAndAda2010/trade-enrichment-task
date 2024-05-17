package com.verygoodbank.tes.controller.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder(toBuilder = true)
public class Trade {

    @CsvBindByName(column = "date")
    private String tradeDate;

    @CsvBindByName(column = "product_id")
    private Long productId;

    @CsvBindByName(column = "currency")
    private String currency;

    @CsvBindByName(column = "Trade ID")
    private Long tradeId;

    @CsvBindByName(column = "price")
    //TODO consider BigDecimal?
    private String price;

    private String productName;

    public static Trade from(final String[] csvRow) {
        return Trade.builder()
                .tradeDate(csvRow[0])
                .productId(Long.parseLong(csvRow[1]))
                .currency(csvRow[2])
                .price(csvRow[3])
                .build();
    }

    @Override
    public String toString() {
        return String.join(",", tradeDate, productName, currency, price) + "\n";
    }

    public byte[] toCSVBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
