package com.verygoodbank.tes.controller.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

    @CsvBindByName(column = "product_id")
    private Long productId;

    @CsvBindByName(column = "product_name")
    private String productName;


}
