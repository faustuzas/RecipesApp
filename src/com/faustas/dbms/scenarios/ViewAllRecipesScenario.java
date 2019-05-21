package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Identifiable;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;
import java.util.stream.IntStream;

@Service(singleton = false)
public class ViewAllRecipesScenario extends ViewRecipesScenario {

    private List<Recipe> recipes;

    public ViewAllRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                                  RecipeService recipeService, NumberReader numberReader) {
        super(interactor, applicationContext, recipeService, numberReader);
        this.recipes = recipeService.findAll();
    }

    @Override
    List<? extends Identifiable> getSelectables() {
        return recipes;
    }

    @Override
    String getTitle() {
        return "All recipes";
    }

    @Override
    void printRecipes() {
        if (recipes.size() == 0) {
            interactor.printCentered("There are no recipes at the moment");
            return;
        }

        IntStream.range(0, recipes.size()).forEach(i ->{
            Recipe recipe = recipes.get(i);
            interactor.print(String.format("* #%d %s (%s minutes)",
                    recipe.getId(), recipe.getTitle(), recipe.getMinutesToPrepare()));
        });
    }
}
