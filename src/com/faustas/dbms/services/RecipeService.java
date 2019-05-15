package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.TopRecipe;
import com.faustas.dbms.repositories.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    private RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Integer id) {
        return recipeRepository.findById(id);
    }

    public List<TopRecipe> findTop() {
        return recipeRepository.findTop();
    }
}
