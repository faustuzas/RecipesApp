package com.faustas.dbms.scenarios;

import com.faustas.dbms.services.ConsoleInteractor;

public abstract class ConsoleScenario {

    ConsoleInteractor interactor;

    public ConsoleScenario(ConsoleInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * @return flag indicating to quit program
     */
    public abstract boolean action();

    void printHelp() {
        interactor.print("Please enter one of the provided choices' number");
    }

}
