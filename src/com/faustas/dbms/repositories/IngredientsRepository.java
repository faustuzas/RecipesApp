package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Ingredient;

import java.util.List;

@Repository(Ingredient.class)
public interface IngredientsRepository {

    @Select("SELECT * FROM ingredients WHERE id = #id")
    @Results({
            @Result(column = "product_id", property = "product", exec = @Exec(aClass = ProductRepository.class))
    })
    Ingredient findById(@Param("id") Integer id);

    @Select("SELECT * FROM ingredients")
    @Results({
            @Result(column = "product_id", property = "product", exec = @Exec(aClass = ProductRepository.class))
    })
    List<Ingredient> findAll();
}
