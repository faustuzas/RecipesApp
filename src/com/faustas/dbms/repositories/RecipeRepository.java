package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.User;

import java.util.List;

@Repository(Recipe.class)
public interface RecipeRepository {

    @Select("SELECT * FROM recipes")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "ingredients", exec = @Exec(aClass = IngredientRepository.class, method = "findByRecipeId")),
            @Result(column = "id", property = "reviews", exec = @Exec(aClass = ReviewRepository.class, method = "findByReviewId")),
            @Result(column = "minutes_to_prepare", property = "minutesToPrepare"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<Recipe> findAll();

    @Select("SELECT * FROM recipes WHERE id = #id")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "ingredients", exec = @Exec(aClass = IngredientRepository.class, method = "findByRecipeId")),
            @Result(column = "id", property = "reviews", exec = @Exec(aClass = ReviewRepository.class, method = "findByReviewId")),
            @Result(column = "minutes_to_prepare", property = "minutesToPrepare"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    Recipe findById(@Param("id") Integer id);

    @Select("SELECT * FROM recipes WHERE title ILIKE '%#title%'")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<Recipe> searchByName(@Param("title") String title);

    @Insert("INSERT INTO recipes (title, description, minutes_to_prepare, author_id) " +
            "VALUES (#r.title, #r.description, #r.minutesToPrepare, #u.id)")
    void insertForUser(@Param("r") Recipe recipe, @Param("u") User user);

    @Update("UPDATE recipes " +
            "SET title = #r.title, description = #r.description, " +
            "minutes_to_prepare = #r.minutesToPrepare " +
            "WHERE id = #r.id")
    Integer update(@Param("r") Recipe recipe);

    @Delete("DELETE FROM recipes WHERE id = #r.id")
    void delete(@Param("r") Recipe recipe);

    @Delete("DELETE FROM recipes WHERE id IN (@ids)")
    void delete(@Param("ids") Integer[] ids);
}
