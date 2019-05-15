package com.faustas.dbms.models;

import com.faustas.dbms.interfaces.Identifiable;

public class TopRecipe implements Identifiable {

    private Integer recipeId;

    private String title;

    private String authorName;

    private Double averageStars;

    @Override
    public Integer getId() {
        return recipeId;
    }

    public void setId(Integer id) {
        this.recipeId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Double getAverageStars() {
        return averageStars;
    }

    public void setAverageStars(Double averageStars) {
        this.averageStars = averageStars;
    }
}
