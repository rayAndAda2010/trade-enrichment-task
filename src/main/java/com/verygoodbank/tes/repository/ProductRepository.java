package com.verygoodbank.tes.repository;



import com.verygoodbank.tes.controller.model.Product;

import java.util.Collection;

public interface ProductRepository {

    Collection<Product> findAll();

}
