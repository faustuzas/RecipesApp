package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.Param;
import com.faustas.dbms.framework.annotations.Repository;
import com.faustas.dbms.framework.annotations.Select;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.models.Recipe;

import java.util.List;

@Repository(Recipe.class)
public interface RecipeRepository {

    @Select("SELECT * FROM recipes WHERE id = #id")
    Product findById(@Param("id") Integer id);

    @Select("SELECT * FROM products")
    List<Product> findAll();
}
