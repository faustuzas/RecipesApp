package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;

import java.util.List;

@Service
public class ViewAllRecipesScenario extends ViewRecipesScenario {

    public ViewAllRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                                  RecipeService recipeService) {
        super(interactor, applicationContext, recipeService);
    }

    @Override
    List<Recipe> selectRecipes() {
        return recipeService.findAll();
    }

    @Override
    String getTitle() {
        return "All recipes";
    }
}
