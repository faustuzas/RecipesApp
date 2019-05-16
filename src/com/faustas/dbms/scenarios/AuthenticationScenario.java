package com.faustas.dbms.scenarios;

import com.faustas.dbms.exceptions.AuthenticationFailed;
import com.faustas.dbms.exceptions.RegistrationFailedException;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Authenticator;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.utils.UserRegistrationInfo;

@Service
public class AuthenticationScenario extends ConsoleScenario {

    private Authenticator authenticator;

    public AuthenticationScenario(ConsoleInteractor interactor, Authenticator authenticator) {
        super(interactor);
        this.authenticator = authenticator;
    }

    @Override
    public boolean action() {
        while (true) {
            interactor.print("1. Login");
            interactor.print("2. Register");
            interactor.print("Q. Exit");

            switch (interactor.getString()) {
                case "1": {
                    String email = interactor.getString("Your email > ");
                    interactor.print("Enter your password");
                    String password = interactor.getPassword();
                    try {
                        authenticator.login(email, password);
                        interactor.printSuccess("Successfully authenticated");
                        return true;
                    } catch (AuthenticationFailed authenticationFailed) {
                        interactor.printError("User authentication failed. Try again");
                    }
                    break;
                }
                case "2": {
                    UserRegistrationInfo.Builder builder = new UserRegistrationInfo.Builder();

                    String name = interactor.getString("Your name > ");
                    builder.name(name);

                    String email = interactor.getString("Your email > ");
                    builder.email(email);

                    while (true) {
                        interactor.print("Enter your password");
                        String password = interactor.getPassword();
                        interactor.print("Repeat your password");
                        String repeatedPassword = interactor.getPassword();
                        if (!repeatedPassword.equals(password)) {
                            interactor.printError("Passwords do not match");
                            continue;
                        }
                        builder.password(password);

                        try {
                            authenticator.register(builder.build());
                            interactor.printSuccess("Successfully registered");
                            return true;
                        } catch (RegistrationFailedException e) {
                            // TODO: Refactor into better exception handling
                            if (e.getMessage().contains("users_email_key")) {
                                interactor.printError("User with this email already exists");
                            } else if (e.getMessage().contains("users_email_check")) {
                                interactor.printError("Make sure you provided valid email");
                            } else {
                                interactor.printError("Registration failed. Make sure you provided good data");
                            }
                            break;
                        }
                    }
                    break;
                }
                case "Q": case "q": return false;
                default: printHelp();
            }
        }
    }
}
