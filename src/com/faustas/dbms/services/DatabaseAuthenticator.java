package com.faustas.dbms.services;

import com.faustas.dbms.exceptions.AuthenticationFailed;
import com.faustas.dbms.exceptions.RegistrationFailedException;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Authenticator;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.models.User;
import com.faustas.dbms.utils.UserRegistrationInfo;

@Service
public class DatabaseAuthenticator implements Authenticator {

    private UserService userService;

    private SecurityContext securityContext;

    public DatabaseAuthenticator(UserService userService, SecurityContext securityContext) {
        this.userService = userService;
        this.securityContext = securityContext;
    }

    @Override
    public void login(String email, String password) throws AuthenticationFailed {
        User user = userService.findByEmailAndPassword(email, password);
        if (user == null) {
            throw new AuthenticationFailed();
        }

        securityContext.setAuthenticatedUser(user);
    }

    @Override
    public void register(UserRegistrationInfo info) throws RegistrationFailedException {
        User user = new User();
        user.setName(info.getName());
        user.setEmail(info.getEmail());
        user.setPassword(info.getPassword());
        try {
            userService.save(user);
        } catch (Exception e) {
            throw new RegistrationFailedException(e.getMessage());
        }
    }
}
