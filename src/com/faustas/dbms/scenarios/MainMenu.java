package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.ScenarioProvider;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.services.ConsoleInteractor;

@Service
public class MainMenu implements ScenarioProvider {

    private SecurityContext securityContext;

    private ApplicationContext applicationContext;

    private ConsoleInteractor interactor;

    public MainMenu(ApplicationContext applicationContext, SecurityContext securityContext,
                    ConsoleInteractor interactor) {
        this.applicationContext = applicationContext;
        this.securityContext = securityContext;
        this.interactor = interactor;
    }

    @Override
    public ConsoleScenario getScenario() {
//        if (securityContext.getAuthenticatedUser() == null) {
//            return applicationContext.getBean(AuthenticationScenario.class);
//        }

        return getScenarioFromMenu();
    }

    private ConsoleScenario getScenarioFromMenu() {
        interactor.printHeader("MAIN MENU");
        interactor.print("1. View top recipes");
        interactor.print("2. View all recipes");
        interactor.print("3. Add a recipe");
        interactor.print("4. View your recipes");
        interactor.print("5. Logout");
        interactor.print("Q. Exit");

        while (true) {
            switch (interactor.getString()) {
                case "1":
                    return applicationContext.getBean(ViewAllRecipesScenario.class);
                case "2":
                    return null;
                case "3":
                    return null;
                case "4":
                    return null;
                case "5":
                    return applicationContext.getBean(LogoutScenario.class);
                case "Q": case "q":
                    return applicationContext.getBean(ExitScenario.class);
                default:
                    printHelp();
            }
        }
    }

    void printHelp() {
        interactor.print("Please enter one of the provided choices' number");
    }
}
