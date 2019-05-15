package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Recipe;
import com.faustas.dbms.models.Review;
import com.faustas.dbms.models.User;
import com.faustas.dbms.repositories.ReviewRepository;

@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addFrom(Review review, User user, Recipe recipe) {
        reviewRepository.insertFromUser(review, user, recipe);
    }
}