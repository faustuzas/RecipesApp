package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.Review;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ReviewService;
import com.faustas.dbms.utils.NumberReader;

@Service(singleton = false)
public class WriteReviewScenario extends ConsoleScenario {

    private ReviewService reviewService;

    private SecurityContext securityContext;

    private Recipe recipe;

    private NumberReader numberReader;

    public WriteReviewScenario(ConsoleInteractor interactor, ReviewService reviewService,
                               NumberReader numberReader, SecurityContext securityContext,
                               @Value("recipe") Recipe recipe) {
        super(interactor);
        this.recipe = recipe;
        this.reviewService = reviewService;
        this.securityContext = securityContext;
        this.numberReader = numberReader;
    }

    @Override
    public boolean action() {
        interactor.printHeader(String.format("Review for \"%s\"", recipe.getTitle()));

        String comment = interactor.ask("Enter your opinion:");
        interactor.print("Stars (0-5):");
        Integer stars = numberReader.readInteger("Stars > ", "Please enter natural number from 0 to 5");

        Review review = new Review();
        review.setComment(comment);
        review.setStars(stars);

        reviewService.addFrom(review, securityContext.getAuthenticatedUser(), recipe);
        interactor.printSuccess("Review added!");

        return true;
    }
}
