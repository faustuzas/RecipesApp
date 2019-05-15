package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.services.ConsoleInteractor;

@Service
public class ExitScenario extends ConsoleScenario {

    public ExitScenario(ConsoleInteractor interactor, ApplicationContext applicationContext) {
        super(interactor, applicationContext);
    }

    @Override
    public boolean action() {
        return false;
    }
}
