package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.TopRecipe;
import com.faustas.dbms.models.User;
import com.faustas.dbms.repositories.IngredientRepository;
import com.faustas.dbms.repositories.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    private RecipeRepository recipeRepository;

    private IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Integer id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> findByUser(User user) {
        return recipeRepository.findByUser(user);
    }

    public List<TopRecipe> findTop() {
        return recipeRepository.findTop();
    }

    public void insertForUser(Recipe recipe, User user) {
        recipeRepository.startTransaction();
        try {
            Integer recipeId = recipeRepository.insertForUser(recipe, user);
            recipe.setId(recipeId);

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientRepository.insertForRecipe(ingredient, recipe);
            }
            recipeRepository.commit();
        } catch (Exception e) {
            recipeRepository.rollback();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(List<Integer> recipesIds) {
        recipeRepository.delete(recipesIds);
    }
}
