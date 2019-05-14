package com.faustas.dbms.interfaces;

import com.faustas.dbms.models.User;

public interface SecurityContext {

    User getAuthenticatedUser();

    void setAuthenticatedUser(User user);

    void clear();
}
