package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.MapBuilder;

import java.util.List;

public abstract class ViewRecipesScenario extends ConsoleScenario {

    RecipeService recipeService;

    ApplicationContext applicationContext;

    public ViewRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext, RecipeService recipeService) {
        super(interactor);
        this.recipeService = recipeService;
        this.applicationContext = applicationContext;
    }

    abstract List<Recipe> selectRecipes();

    abstract String getTitle();

    @Override
    public boolean action() {
        interactor.printHeader(getTitle());

        List<Recipe> recipes = selectRecipes();
        printRecipes(recipes);

        return selectOption(recipes).action();
    }

    /**
     * Can be overridden to extend functionality
     */
    ConsoleScenario selectOption(List<Recipe> recipes) {
        interactor.printSeparator();
        while (true) {
            interactor.print("1. View recipe");
            interactor.print("Q. Back");

            switch (interactor.getString()) {
                case "1":
                    try {
                        Integer recipeId = Integer.valueOf(interactor.getString("Recipe id > "));
                        // Check if given id is in the list
                        recipes.stream().filter(r -> r.getId().equals(recipeId)).findFirst()
                                .orElseThrow(NumberFormatException::new);

                        return applicationContext.getBean(ViewRecipeScenario.class, MapBuilder.ofPair("recipeId", recipeId));
                    } catch (NumberFormatException e) {
                        interactor.printError("Enter valid recipe id");
                        break;
                    }
                case "Q":
                    return applicationContext.getBean(BackScenario.class);
                default:
                    printHelp();
            }
        }
    }

    private void printRecipes(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            interactor.print(String.format(
                    "%d. %s (%s minutes) Stars: %.2f", recipe.getId(), recipe.getTitle(), recipe.getMinutesToPrepare(), recipe.getAverageStars()));
        }
    }
}
