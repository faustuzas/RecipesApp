package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.User;

@Service
public class SecurityContextImpl implements SecurityContext {

    private ThreadLocal<User> userHolder = ThreadLocal.withInitial(() -> null);

    @Override
    public synchronized User getAuthenticatedUser() {
        return userHolder.get();
    }

    @Override
    public synchronized void setAuthenticatedUser(User user) {
        this.userHolder.set(user);
    }

    @Override
    public void clear() {
        this.userHolder.remove();
    }
}
