package com.verygoodbank.tes.repository.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import com.verygoodbank.tes.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import com.verygoodbank.tes.controller.model.Product;
import java.io.*;
import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Collection<Product> findAll() {
        // Simulate product data
        final InputStream prdIns = getClass().getResourceAsStream("/product.csv");
        final List<Product> products = new CsvToBeanBuilder<Product>(new InputStreamReader(prdIns))
                .withType(Product.class)
                .build()
                .parse();
        log.info("{} product(s) retrieved from DB", products.size());
        return products;

    }

}
