package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.NumberReader;

import java.util.ArrayList;
import java.util.List;

@Service(singleton = false)
public class UpdateRecipeScenario extends RecipeCRUDScenario {

    private Recipe recipe;

    private String newTitle;
    private String newDescription;
    private Integer newMinutesToPrepare;
    private List<Ingredient> newIngredients;

    public UpdateRecipeScenario(ConsoleInteractor interactor, ProductService productService,
                                NumberReader numberReader, RecipeService recipeService,
                                @Value("recipeId") Integer recipeId) {
        super(interactor, productService, numberReader, recipeService);
        this.recipe = recipeService.findById(recipeId);

        this.newTitle = recipe.getTitle();
        this.newDescription = recipe.getDescription();
        this.newMinutesToPrepare = recipe.getMinutesToPrepare();
        this.newIngredients = new ArrayList<>(recipe.getIngredients());
    }

    @Override
    public boolean action() {
        interactor.printHeader(String.format("Update \"%s\" recipe", recipe.getTitle()));

        while (true) {
            printNewRecipeInfo();

            interactor.printSeparator();
            interactor.print("1. Update title");
            interactor.print("2. Update description");
            interactor.print("3. Update preparation time");
            interactor.print("4. Update ingredients");
            interactor.print("5. Save");
            interactor.print("6. Cancel");

            switch (interactor.getString()) {
                case "1":
                    updateTitle();
                    break;
                case "2":
                    updateDescription();
                    break;
                case "3":
                    updatePreparationTime();
                    break;
                case "4":
                    updateIngredients();
                    break;
                case "5":
                    try {
                        save();
                        interactor.printSuccess("Recipe updated successfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                        interactor.printError(e.getMessage());
                        interactor.printError("Error while updating recipe");
                    }
                    return true;
                case "6":
                    return false;
                default:
                    printHelp();
            }
        }
    }

    private void updateTitle() {
        interactor.printHeader("New title");
        newTitle = interactor.getString();
    }

    private void updateDescription() {
        interactor.printHeader("New description");
        newDescription = interactor.getString();
    }

    private void updatePreparationTime() {
        interactor.printHeader("New preparation time in minutes");
        newMinutesToPrepare = numberReader.readInteger("Minutes > ", "Please enter only number of minutes");
    }

    private void updateIngredients() {
        interactor.printHeader("Update ingredients");

        updateLoop:
        while (true) {
            printIngredients(newIngredients);
            interactor.printSeparator();
            interactor.print("1. Add new ingredient");
            interactor.print("2. Delete ingredient");
            interactor.print("3. Done");

            switch (interactor.getString()) {
                case "1":
                    Ingredient ingredient = createNewIngredient();
                    if (ingredient == null) {
                        break;
                    }
                    newIngredients.add(ingredient);
                    interactor.printSuccess("Ingredient added");
                    break;
                case "2":
                    // because arrays start from 0, subtract one
                    int ingredientId = numberReader.readInteger("Ingredient number > ", "Enter only number") - 1;

                    if (!(0 <= ingredientId && ingredientId < newIngredients.size())) {
                        interactor.printError("Ingredient with provided id does not exist");
                        break;
                    }

                    newIngredients.remove(ingredientId);
                    interactor.printSuccess("Ingredient removed");
                    break;
                case "3":
                    break updateLoop;
                default:
                    printHelp();
            }
        }
    }

    private void printNewRecipeInfo() {
        Recipe newRecipe = new Recipe();
        newRecipe.setTitle(newTitle);
        newRecipe.setDescription(newDescription);
        newRecipe.setMinutesToPrepare(newMinutesToPrepare);
        newRecipe.setIngredients(newIngredients);

        printRecipeInfo(newRecipe);
    }

    private void printIngredients(List<Ingredient> ingredients) {
        for (int i = 0; i < ingredients.size(); ++i) {
            Ingredient ingredient = ingredients.get(i);
            interactor.print(String.format("%d. %s (%s)", i + 1,
                    ingredient.getProduct().getName(), ingredient.getAmount()));
        }
    }

    private void save() {
        Recipe newRecipe = new Recipe();
        newRecipe.setTitle(newTitle);
        newRecipe.setDescription(newDescription);
        newRecipe.setIngredients(newIngredients);
        newRecipe.setMinutesToPrepare(newMinutesToPrepare);

        recipeService.update(recipe, newRecipe);
    }
}