package com.verygoodbank.tes.repository;

import com.verygoodbank.tes.repository.impl.ProductRepositoryImpl;
import org.junit.jupiter.api.Test;

public class ProductRepositoryImplTest {

    private ProductRepository productRepository;


    @Test
    void givenProductDataSource_whenCallingFindAll_thenReturnAllProducts() {
        productRepository = new ProductRepositoryImpl();

        System.out.println(productRepository.findAll());
    }

}
