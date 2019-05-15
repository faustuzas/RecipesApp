package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.services.ConsoleInteractor;

public abstract class ConsoleScenario {

    ConsoleInteractor interactor;

    ApplicationContext applicationContext;

    public ConsoleScenario(ConsoleInteractor interactor, ApplicationContext applicationContext) {
        this.interactor = interactor;
        this.applicationContext = applicationContext;
    }

    /**
     * @return flag indicating to quit program
     */
    public abstract boolean action();

    void printHelp() {
        interactor.print("Please enter one of the provided choices' number");
    }
}
