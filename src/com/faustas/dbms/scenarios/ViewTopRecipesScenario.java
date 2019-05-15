package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Identifiable;
import com.faustas.dbms.models.TopRecipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;

@Service(singleton = false)
public class ViewTopRecipesScenario extends ViewRecipesScenario {

    List<TopRecipe> topRecipes;

    public ViewTopRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                                  RecipeService recipeService, NumberReader numberReader) {
        super(interactor, applicationContext, recipeService, numberReader);
        this.topRecipes = recipeService.findTop();
    }

    @Override
    List<? extends Identifiable> getSelectables() {
        return topRecipes;
    }

    @Override
    String getTitle() {
        return "TOP RECIPES";
    }

    @Override
    void printRecipes() {
        if (topRecipes.size() == 0) {
            interactor.printCentered("There are no recipes at the moment");
            return;
        }

        for (int i = 0; i < topRecipes.size(); ++i) {
            TopRecipe topRecipe = topRecipes.get(i);
            interactor.print(String.format("#%d ID-%d \"%s\" (%.2f/5) by %s", i + 1, topRecipe.getId(), topRecipe.getTitle(),
                    topRecipe.getAverageStars(), topRecipe.getAuthorName()));
        }
    }
}
