package com.verygoodbank.tes.repository;

import com.verygoodbank.tes.repository.impl.ProductRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductRepositoryImplTest {

    @Test
    void givenProductDataSource_whenCallingFindAll_thenReturnAllProducts() {
        ProductRepository productRepository = new ProductRepositoryImpl();
        Assertions.assertEquals(10, productRepository.findAll().size());
    }

}
