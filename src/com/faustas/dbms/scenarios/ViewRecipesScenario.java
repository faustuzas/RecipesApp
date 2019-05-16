package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.interfaces.Identifiable;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.MapBuilder;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;

public abstract class ViewRecipesScenario extends ConsoleScenario {

    static final Integer EXTEND_FROM_OPTION = 2;

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

    /**
     * Can be overridden to extend functionality
     */
    void printMoreOptions() { }

    /**
     * Can be overridden to extend functionality
     */
    void handleSelection(String selection) {
        printHelp();
    }

    @Override
    public boolean action() {
        do {
            interactor.printHeader(getTitle());
            printRecipes();
        } while (selectOption().action());

        return true;
    }

    final ConsoleScenario selectOption() {
        interactor.printSeparator();
        while (true) {
            interactor.print("1. View recipe");
            printMoreOptions();
            interactor.print("Q. Back");

            String selection = interactor.getString();
            switch (selection) {
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
                    handleSelection(selection);
            }
        }
    }
}
