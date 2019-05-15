package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.SecurityContext;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.utils.ConsoleColor;

@Service
public class LogoutScenario extends ConsoleScenario {

    private SecurityContext securityContext;

    public LogoutScenario(ConsoleInteractor interactor, SecurityContext securityContext) {
        super(interactor);
        this.securityContext = securityContext;
    }

    @Override
    public boolean action() {
        securityContext.clear();
        interactor.printSuccess("Logout successful");
        return true;
    }
}
