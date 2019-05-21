package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Identifiable;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.MapBuilder;
import com.faustas.dbms.utils.NumberReader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service(singleton = false)
public class ViewYourRecipesScenario extends ViewRecipesScenario {

    private List<Recipe> recipes;

    private SecurityContext securityContext;

    public ViewYourRecipesScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                                   RecipeService recipeService, NumberReader numberReader,
                                   SecurityContext securityContext) {
        super(interactor, applicationContext, recipeService, numberReader);
        this.securityContext = securityContext;
        setAuthenticatedRecipes();
    }

    @Override
    List<? extends Identifiable> getSelectables() {
        return recipes;
    }

    @Override
    String getTitle() {
        return "Your recipes";
    }

    @Override
    void printRecipes() {
        for (Recipe recipe : recipes) {
            interactor.print(String.format(
                    "* #%d %s @%tF", recipe.getId(), recipe.getTitle(), recipe.getCreatedAt()));
        }
    }

    @Override
    void printMoreOptions() {
        interactor.print(String.format("%d. Update recipe", EXTEND_FROM_OPTION));
        interactor.print(String.format("%d. Delete recipes", EXTEND_FROM_OPTION + 1));
    }

    @Override
    void handleSelection(String selection) {
        if (String.valueOf(EXTEND_FROM_OPTION).equals(selection)) {
            handleUpdate();
            return;
        }

        if (String.valueOf(EXTEND_FROM_OPTION + 1).equals(selection)) {
            handleDeletion();
            return;
        }

        printHelp();
    }

    void handleUpdate() {
        while (true) {
            Integer recipeId = numberReader.readInteger("Recipe id > ", "Enter only id");
            long recipesWithId = recipes.stream().filter(r -> r.getId().equals(recipeId)).count();
            if (recipesWithId == 1) {
                boolean recipeUpdated = applicationContext.getBean(UpdateRecipeScenario.class, MapBuilder.ofPair("recipeId", recipeId)).action();
                if (recipeUpdated) {
                    setAuthenticatedRecipes();
                }
                break;
            }

            interactor.printError("Recipe with provided id does not exist");
        }
    }

    private void handleDeletion() {
        interactor.printHelp("Enter recipes' ids separated by comma");
        List<Integer> recipesIds;
        while (true) {
            try {
                recipesIds = Arrays.stream(interactor.getString().split(","))
                        .map(String::trim).map(this::convertToInt).collect(Collectors.toList());
                break;
            } catch (Exception e) {
                interactor.printError("Enter only recipe ids separated by comma");
            }
        }

        try {
            recipeService.delete(recipesIds);
            interactor.printSuccess("Recipes deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            interactor.printError("Could not delete recipes");
        }
    }

    private Integer convertToInt(String integer) {
        try {
            return Integer.valueOf(integer);
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    private void setAuthenticatedRecipes() {
        this.recipes = recipeService.findByUser(securityContext.getAuthenticatedUser());
    }
}
