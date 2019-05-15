package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.Review;
import com.faustas.dbms.models.User;

import java.util.List;

@Repository(Review.class)
public interface ReviewRepository {

    @Select("SELECT * FROM reviews")
    @Results({
            @Result(column = "created_at", property = "createdAt")
    })
    List<Review> findAll();

    @Select("SELECT * FROM reviews WHERE id = #id")
    @Results({
            @Result(column = "created_at", property = "createdAt")
    })
    Review findById(@Param("id") Integer id);

    @Select("SELECT * FROM reviews WHERE recipe_id = #recipeId")
    @Results({
            @Result(column = "created_at", property = "createdAt")
    })
    List<Review> findByRecipeId(@Param("recipeId") Integer recipeId);

    @Insert("INSERT INTO reviews (comment, stars, user_id, recipe_id) " +
            "VALUES (#r.comment, #r.stars, #u.id, #re.id)")
    void insertFromUser(@Param("r") Review review, @Param("u") User user, @Param("re") Recipe recipe);

    @Update("UPDATE reviews " +
            "SET comment = #r.comment, stars = #r.stars, created_at = #r.createdAt" +
            "WHERE id = #r.id")
    Review update(@Param("r") Review review);

    @Delete("DELETE FROM reviews WHERE id = #r.id")
    void delete(@Param("r") Review review);
}
