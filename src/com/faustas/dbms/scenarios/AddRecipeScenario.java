package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.NumberReader;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddRecipeScenario extends ConsoleScenario {

    private RecipeService recipeService;

    private NumberReader numberReader;

    private ProductService productService;

    private SecurityContext securityContext;

    public AddRecipeScenario(ConsoleInteractor interactor, RecipeService recipeService,
                             NumberReader numberReader, ProductService productService,
                             SecurityContext securityContext) {
        super(interactor);
        this.recipeService = recipeService;
        this.numberReader = numberReader;
        this.productService = productService;
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
        Integer minutesToPrepare = numberReader.readInteger("Please enter only number");
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
                                "* ID-%d %s", product.getId(), product.getName()));
                    }

                    interactor.print("1. Select");
                    interactor.print("2. Cancel");
                    selectProductLoop:
                    while (true) {
                        switch (interactor.getString()) {
                            case "1":
                                Product product = null;
                                while (product == null) {
                                    Integer productId = numberReader.readInteger("Enter only product id");
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
