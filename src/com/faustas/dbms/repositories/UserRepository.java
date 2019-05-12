package com.faustas.dbms.repositories;

import com.faustas.dbms.framework.annotations.Repository;
import com.faustas.dbms.framework.annotations.Result;
import com.faustas.dbms.framework.annotations.Results;
import com.faustas.dbms.framework.annotations.Select;
import com.faustas.dbms.models.User;

import java.util.List;

@Repository(User.class)
public interface UserRepository {

    @Select("SELECT * FROM users")
    @Results({
            @Result(column = "created_at", property = "createdAt")
    })
    List<User> findAll();
}
