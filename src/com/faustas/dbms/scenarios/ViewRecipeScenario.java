package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.Review;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.RecipeService;

import java.util.List;

@Service(singleton = false)
public class ViewRecipeScenario extends ConsoleScenario {

    private RecipeService recipeService;

    private Recipe recipe;

    public ViewRecipeScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                              RecipeService recipeService, @Value("recipeId") Integer recipeId) {
        super(interactor, applicationContext);
        this.recipeService = recipeService;
        this.recipe = recipeService.findById(recipeId);
    }

    @Override
    public boolean action() {
        interactor.printWithBorderAndColor(recipe.getTitle());
        interactor.print(recipe.getDescription());
        interactor.printSectionHeader("Ingredients:");
        for (Ingredient ingredient : recipe.getIngredients()) {
            interactor.print(String.format("* %s (%s)", ingredient.getProduct().getName(), ingredient.getAmount()));
        }

        List<Review> reviews = recipe.getReviews();
        if (reviews.size() > 0) {
            interactor.printSectionHeader("Reviews:");
            for (Review review : reviews) {
                interactor.print(String.format(
                        "* %d/5 %s", review.getStars(), review.getComment()));
            }
        }

        return true;
    }
}
