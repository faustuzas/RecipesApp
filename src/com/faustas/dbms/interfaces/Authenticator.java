package com.faustas.dbms.interfaces;

import com.faustas.dbms.exceptions.AuthenticationFailed;
import com.faustas.dbms.exceptions.RegistrationFailedException;
import com.faustas.dbms.utils.UserRegistrationInfo;

public interface Authenticator {

    void login(String email, String password) throws AuthenticationFailed;

    void register(UserRegistrationInfo info) throws RegistrationFailedException;
}
