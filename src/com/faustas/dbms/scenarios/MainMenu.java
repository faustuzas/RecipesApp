package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.ScenarioProvider;
import com.faustas.dbms.interfaces.SecurityContext;

@Service
public class MainMenu implements ScenarioProvider {

    private SecurityContext securityContext;

    private ApplicationContext applicationContext;

    public MainMenu(ApplicationContext applicationContext, SecurityContext securityContext) {
        this.applicationContext = applicationContext;
        this.securityContext = securityContext;
    }

    @Override
    public ConsoleScenario getScenario() {
        if (securityContext.getAuthenticatedUser() == null) {
            return applicationContext.getBean(AuthenticationConsoleScenario.class);
        }

        return null;
    }
}
