package com.faustas.dbms.scenarios;

import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.ConsoleColor;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;

abstract class RecipeCRUDScenario extends ConsoleScenario {

    ProductService productService;

    NumberReader numberReader;

    RecipeService recipeService;

    public RecipeCRUDScenario(ConsoleInteractor interactor, ProductService productService,
                              NumberReader numberReader, RecipeService recipeService) {
        super(interactor);
        this.productService = productService;
        this.numberReader = numberReader;
        this.recipeService = recipeService;
    }

    void printRecipeInfo(Recipe recipe) {
        interactor.print(recipe.getTitle());
        interactor.printWithColor(String.format("Preparation time: %s minutes", recipe.getMinutesToPrepare()), ConsoleColor.PURPLE);
        interactor.print(recipe.getDescription());
        interactor.printSectionHeader("Ingredients:");
        for (Ingredient ingredient : recipe.getIngredients()) {
            interactor.print(String.format("* %s (%s)", ingredient.getProduct().getName(), ingredient.getAmount()));
        }
    }

    Ingredient createNewIngredient() {
        while (true) {
            interactor.printSeparator();
            interactor.print("1. Search for product");
            interactor.print("2. Cancel");

            switch (interactor.getString()) {
                case "1":
                    String productName = interactor.ask("Enter product name: ");
                    List<Product> foundProducts = productService.searchByName(productName);
                    if (foundProducts.size() == 0) {
                        interactor.print("No products found");
                        break;
                    }

                    interactor.print(String.format("%d products found:", foundProducts.size()));
                    for (Product product : foundProducts) {
                        interactor.print(String.format(
                                "* #%d %s", product.getId(), product.getName()));
                    }

                    interactor.print("1. Select");
                    interactor.print("2. Cancel");
                    selectProductLoop:
                    while (true) {
                        switch (interactor.getString()) {
                            case "1":
                                Product product = null;
                                while (product == null) {
                                    Integer productId = numberReader.readInteger("Product id > ", "Enter only id");
                                    product = foundProducts.stream().filter(p -> p.getId().equals(productId))
                                            .findFirst().orElseGet(() -> {
                                                interactor.printError("Product with this id does not exist");
                                                return null;
                                            });
                                }

                                String amount = interactor.ask("Enter amount for " + product.getName());

                                Ingredient ingredient = new Ingredient();
                                ingredient.setAmount(amount);
                                ingredient.setProduct(product);

                                return ingredient;
                            case "2":
                                break selectProductLoop;
                            default:
                                printHelp();
                        }
                    }
                case "2":
                    return null;
                default:
                    printHelp();
            }
        }
    }
}
