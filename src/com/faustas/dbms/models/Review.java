package com.faustas.dbms.models;

import com.faustas.dbms.interfaces.Identifiable;

import java.util.Date;

public class Review implements Identifiable {

    private Integer id;

    private String comment;

    private Integer stars;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", stars=" + stars +
                ", createdAt=" + createdAt +
                '}';
    }
}
