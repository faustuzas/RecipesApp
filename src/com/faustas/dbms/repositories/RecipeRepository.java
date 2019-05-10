package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.models.Recipe;

import java.util.List;

@Repository(Recipe.class)
public interface RecipeRepository {

    @Select("SELECT * FROM recipes")
    @Results({
            @Result(column = "minutes_to_prepare", property = "minutesToPrepare"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<Recipe> findAll();

    @Select("SELECT * FROM recipes WHERE id = #id")
    Recipe findById(@Param("id") Integer id);

    @Select("SELECT * FROM recipes WHERE title ILIKE '%#title%'")
    List<Recipe> searchByName(@Param("title") String title);

    @Insert("INSERT INTO products (name, carbohydrates, proteins, fat) " +
            "VALUES (#p.name, #p.carbohydrates, #p.proteins, #p.fats)")
    void insert(@Param("p") Product product);

    @Update("UPDATE products " +
            "SET name = #p.name, carbohydrates = #p.carbohydrates, " +
            "proteins = #p.proteins, fat = #p.fat " +
            "WHERE id = #i.id")
    Integer update(@Param("p") Product product);

    @Delete("DELETE FROM products WHERE id = #p.id")
    void delete(@Param("p") Product product);

    @Delete("DELETE FROM products WHERE id IN (@ids)")
    void delete(@Param("ids") Integer[] ids);
}
