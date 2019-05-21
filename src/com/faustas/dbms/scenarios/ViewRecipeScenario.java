package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.Review;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.services.RecipeService;
import com.faustas.dbms.utils.ConsoleColor;
import com.faustas.dbms.utils.MapBuilder;
import com.faustas.dbms.utils.NumberReader;

import java.util.List;

@Service(singleton = false)
public class ViewRecipeScenario extends RecipeCRUDScenario {

    private ApplicationContext applicationContext;

    private Recipe recipe;

    public ViewRecipeScenario(ConsoleInteractor interactor, ProductService productService,
                              NumberReader numberReader, RecipeService recipeService,
                              ApplicationContext applicationContext, @Value("recipeId") Integer recipeId) {
        super(interactor, productService, numberReader, recipeService);
        this.recipe = recipeService.findById(recipeId);
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean action() {
        interactor.printHeader(recipe.getTitle());
        printRecipeInfo(recipe);

        List<Review> reviews = recipe.getReviews();
        if (reviews.size() > 0) {
            interactor.printSectionHeader("Reviews:");
            for (Review review : reviews) {
                interactor.print(String.format(
                        "* %d/5 %s", review.getStars(), review.getComment()));
            }
        }

        interactor.printSeparator();
        while (true) {
            interactor.print("1. Write review");
            interactor.print("Q. Back");

            switch (interactor.getString()) {
                case "1":
                    return applicationContext.getBean(WriteReviewScenario.class, MapBuilder.ofPair("recipe", recipe))
                            .action();
                case "Q": case "q":
                    return true;
                default:
                    printHelp();
            }
        }
    }
}
