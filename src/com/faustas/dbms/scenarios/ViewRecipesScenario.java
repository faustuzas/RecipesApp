package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.interfaces.Identifiable;
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

    abstract List<? extends Identifiable> getSelectables();

    abstract String getTitle();

    abstract void printRecipes();

    @Override
    public boolean action() {
        do {
            interactor.printHeader(getTitle());
            printRecipes();
        } while (selectOption().action());

        return true;
    }

    /**
     * Can be overridden to extend functionality
     */
    ConsoleScenario selectOption() {
        interactor.printSeparator();
        while (true) {
            interactor.print("1. View recipe");
            interactor.print("Q. Back");

            switch (interactor.getString()) {
                case "1":
                    try {
                        Integer recipeId = Integer.valueOf(interactor.getString("Recipe id > "));
                        // Check if given id is in the list
                        getSelectables().stream().filter(s -> s.getId().equals(recipeId)).findFirst()
                                .orElseThrow(NumberFormatException::new);

                        return applicationContext.getBean(ViewRecipeScenario.class, MapBuilder.ofPair("recipeId", recipeId));
                    } catch (NumberFormatException e) {
                        interactor.printError("Enter valid recipe id");
                        break;
                    }
                case "Q": case "q":
                    return applicationContext.getBean(BackScenario.class);
                default:
                    printHelp();
            }
        }
    }
}
