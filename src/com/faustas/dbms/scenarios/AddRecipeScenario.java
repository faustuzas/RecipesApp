package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.NumberReader;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddRecipeScenario extends RecipeCRUDScenario {

    private SecurityContext securityContext;

    public AddRecipeScenario(ConsoleInteractor interactor, ProductService productService,
                             NumberReader numberReader, RecipeService recipeService,
                             SecurityContext securityContext) {
        super(interactor, productService, numberReader, recipeService);
        this.securityContext = securityContext;
    }

    @Override
    public boolean action() {
        interactor.printHeader("Add new recipe");

        Recipe recipe = new Recipe();

        interactor.printSectionHeader("Main information");

        String title = interactor.ask("Recipe title: ");
        recipe.setTitle(title);

        String description = interactor.ask("Recipe description: ");
        recipe.setDescription(description);

        interactor.print("Minutes to prepare");
        Integer minutesToPrepare = numberReader.readInteger("Minutes > ", "Enter only number of minutes");
        recipe.setMinutesToPrepare(minutesToPrepare);

        List<Ingredient> ingredients = new ArrayList<>();

        ingredientsLoop:
        while (true) {
            interactor.printSeparator();
            interactor.print("1. Add ingredient");
            interactor.print("2. Finish adding ingredients");

            switch (interactor.getString()) {
                case "1":
                    Ingredient ingredient = createNewIngredient();
                    if (ingredient == null) {
                        break ingredientsLoop;
                    }

                    ingredients.add(ingredient);
                    break;
                case "2":
                    recipe.setIngredients(ingredients);
                    break ingredientsLoop;
                default:
                    printHelp();
            }
        }

        try {
            recipeService.insertForUser(recipe, securityContext.getAuthenticatedUser());
            interactor.printSuccess(String.format("Recipe \"%s\" successfully added", recipe.getTitle()));
        } catch (Exception e) {
            e.printStackTrace();
            interactor.printError("Recipe with this title already exists");
        }

        return true;
    }
}
