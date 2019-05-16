package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.interfaces.Identifiable;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.MapBuilder;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;

public abstract class ViewRecipesScenario extends ConsoleScenario {

    RecipeService recipeService;

    ApplicationContext applicationContext;

    private NumberReader numberReader;

    public ViewRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                               RecipeService recipeService, NumberReader numberReader) {
        super(interactor);
        this.recipeService = recipeService;
        this.applicationContext = applicationContext;
        this.numberReader = numberReader;
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
                    Integer recipeId = numberReader.readInteger("Enter only recipe id");
                    if (getSelectables().stream().noneMatch(s -> s.getId().equals(recipeId))) {
                        interactor.printError("Recipe does not exist");
                        break;
                    }

                    return applicationContext.getBean(ViewRecipeScenario.class, MapBuilder.ofPair("recipeId", recipeId));
                case "Q": case "q":
                    return applicationContext.getBean(BackScenario.class);
                default:
                    printHelp();
            }
        }
    }
}
