package com.faustas.dbms.models;

import java.util.Date;

public class User {

    private Integer id;

    private String name;

    private String email;

    private String password;

    private Date createdAt;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
