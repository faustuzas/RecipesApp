package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.TopRecipe;
import com.faustas.dbms.models.User;

import java.util.List;

@Repository(Recipe.class)
public interface RecipeRepository {

    @Select("SELECT * FROM recipes")
    List<Recipe> findAll();

    @Select("SELECT * FROM recipes WHERE id = #id")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "ingredients", exec = @Exec(aClass = IngredientRepository.class, method = "findByRecipeId")),
            @Result(column = "id", property = "reviews", exec = @Exec(aClass = ReviewRepository.class, method = "findByRecipeId")),
    })
    Recipe findById(@Param("id") Integer id);

    @Select("SELECT * FROM recipes WHERE title ILIKE '%#title%'")
    List<Recipe> searchByName(@Param("title") String title);

    @Insert("INSERT INTO recipes (title, description, minutes_to_prepare, author_id) " +
            "VALUES (#r.title, #r.description, #r.minutesToPrepare, #u.id)")
    Integer insertForUser(@Param("r") Recipe recipe, @Param("u") User user);

    @Update("UPDATE recipes " +
            "SET title = #r.title, description = #r.description, " +
            "minutes_to_prepare = #r.minutesToPrepare " +
            "WHERE id = #r.id")
    Integer update(@Param("r") Recipe recipe);

    @Delete("DELETE FROM recipes WHERE id = #r.id")
    void delete(@Param("r") Recipe recipe);

    @Delete("DELETE FROM recipes WHERE id IN (@ids)")
    void delete(@Param("ids") Integer[] ids);

    @Select(value = "SELECT * FROM top_recipes_with_authors", resultClass = TopRecipe.class)
    @Results({
            @Result(column = "name", property = "authorName"),
            @Result(column = "recipe_id", property = "id")
    })
    List<TopRecipe> findTop();
}
