package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.utils.ConsoleColor;

@Service
public class LogoutScenario extends ConsoleScenario {

    private SecurityContext securityContext;

    public LogoutScenario(ConsoleInteractor interactor, ApplicationContext applicationContext,
                          SecurityContext securityContext) {
        super(interactor, applicationContext);
        this.securityContext = securityContext;
    }

    @Override
    public boolean action() {
        securityContext.clear();
        interactor.printWithColor("Logout successful", ConsoleColor.GREEN);
        return true;
    }
}
