package com.verygoodbank.tes.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class Trade {

    @CsvBindByName(column = "date")
    private String tradeDate;

    @CsvBindByName(column = "product_id")
    private Long productId;

    @CsvBindByName(column = "currency")
    private String currency;

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

    // A hack to gen header, should consider to native API from opencsv, XD
    public String toCsvHeader() {
        return "date,productName,currency,price" + "\n";
    }

    @Override
    public String toString() {
        return String.join(",", tradeDate, productName, currency, price) + "\n";
    }

    public byte[] toCsvHeaderBytes() {
        return getUTF8Bytes(toCsvHeader());
    }

    public byte[] toCsvRowBytes() {
        return getUTF8Bytes(toString());
    }

    public byte[] getUTF8Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

}
