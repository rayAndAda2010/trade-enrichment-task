package com.verygoodbank.tes.service;

import com.verygoodbank.tes.model.Product;
import com.verygoodbank.tes.repository.ProductRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.verygoodbank.tes.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    void givenRequestGetAllProducts_whenCallingGetAllProducts_thenAllProductsReturnOk() {
        Product p1 = new Product(1L, "ProductA");
        Product p2 = new Product(2L, "ProductB");

        Mockito.doReturn(List.of(p1, p2)).when(productRepository).findAll();
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository);
        final Map<Long, Product> idToProductsMapping = productServiceImpl.getAllProducts().block();

        assertNotEquals(null, idToProductsMapping);
        int size = idToProductsMapping.size();
        assertEquals(2, size);
    }

}
