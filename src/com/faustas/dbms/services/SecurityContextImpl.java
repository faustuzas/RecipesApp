package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.User;

@Service
public class SecurityContextImpl implements SecurityContext {

    private User user;

    @Override
    public synchronized User getAuthenticatedUser() {
        return user;
    }

    @Override
    public synchronized void setAuthenticatedUser(User user) {
        this.user = user;
    }

    @Override
    public void clear() {
        this.user = null;
    }
}
