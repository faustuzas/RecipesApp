package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Product;

import java.util.List;

@Repository(Product.class)
public interface ProductRepository {

    @Select("SELECT * FROM products")
    @Results({
            @Result(column = "carbohydrates_per_100g", property = "carbohydrates"),
            @Result(column = "proteins_per_100g", property = "proteins"),
            @Result(column = "fat_per_100g", property = "fats")
    })
    List<Product> findAll();

    @Select("SELECT * FROM products WHERE id = #id")
    @Results({
            @Result(column = "carbohydrates_per_100g", property = "carbohydrates"),
            @Result(column = "proteins_per_100g", property = "proteins"),
            @Result(column = "fat_per_100g", property = "fats")
    })
    Product findById(@Param("id") Integer id);

    @Select("SELECT * FROM products WHERE name ILIKE #%name%")
    @Results({
            @Result(column = "carbohydrates_per_100g", property = "carbohydrates"),
            @Result(column = "proteins_per_100g", property = "proteins"),
            @Result(column = "fat_per_100g", property = "fats")
    })
    List<Product> searchByName(@Param("name") String name);

    @Insert("INSERT INTO products (name, carbohydrates_per_100g, proteins_per_100g, fat_per_100g) " +
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
