package com.verygoodbank.tes.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @CsvBindByName(column = "product_id")
    private Long productId;

    @CsvBindByName(column = "product_name")
    private String productName;


}
