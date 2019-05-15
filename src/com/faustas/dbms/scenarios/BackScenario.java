package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.services.ConsoleInteractor;

@Service
public class BackScenario extends ConsoleScenario {

    public BackScenario(ConsoleInteractor interactor) {
        super(interactor);
    }

    @Override
    public boolean action() {
        return true;
    }
}
