package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.models.User;

import java.util.List;

@Repository(User.class)
public interface UserRepository {

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Select("SELECT * FROM users WHERE email = #email AND password = #password")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Insert("INSERT INTO users (name, email, password)" +
            "VALUES (#u.name, #u.email, #u.password)")
    void insert(@Param("u") User user);
}