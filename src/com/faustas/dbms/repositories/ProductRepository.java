package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Product;

import java.util.List;

@Repository(Product.class)
public interface ProductRepository {

    @Select("SELECT * FROM products WHERE id = #id")
    @Results({
            @Result(column = "fat", property = "fats")
    })
    Product findById(@Param("id") Integer id);

    @Select("SELECT * FROM products")
    @Results({
            @Result(column = "fat", property = "fats")
    })
    List<Product> findAll();
}
