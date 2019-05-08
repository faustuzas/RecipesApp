package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;

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

    @Update("UPDATE ingredients " +
            "SET amount = #i.amount, product_id = #i.product.id " +
            "WHERE id = #i.id")
    Integer update(@Param("i") Ingredient ingredient);

    @Insert("INSERT INTO ingredients (amount, product_id, recipe_id) " +
            "VALUES (#i.amount, #i.product.id, #r.id)")
    void insert(@Param("i") Ingredient ingredient, @Param("r") Recipe recipe);

    @Delete("DELETE FROM ingredients WHERE id = #i.id")
    void delete(@Param("i") Ingredient ingredient);

    @Delete("DELETE FROM ingredients WHERE id IN (@ids)")
    void delete(@Param("ids") Integer[] ids);
}
