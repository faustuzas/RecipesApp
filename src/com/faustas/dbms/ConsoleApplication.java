package com.faustas.dbms;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.interfaces.Bootable;
import com.faustas.dbms.interfaces.ScenarioProvider;
import com.faustas.dbms.scenarios.ConsoleScenario;
import com.faustas.dbms.services.ConsoleInteractor;

@Service
public class ConsoleApplication implements Bootable {

    private ConsoleInteractor interactor;

    private ScenarioProvider scenarioProvider;

    public ConsoleApplication(ConsoleInteractor interactor, ScenarioProvider scenarioProvider) {
        this.interactor = interactor;
        this.scenarioProvider = scenarioProvider;
    }

    @Override
    public void boot() {
        printTitle();

        while (true) {
            ConsoleScenario scenario = scenarioProvider.getScenario();

            // if action returns false, it means application should quit
            if (scenario == null || !scenario.action()) {
                break;
            }
        }

        printGoodbye();
    }

    private void printTitle() {
        interactor.printWithBorderAndColor("WELCOME TO FAUSTAS' RECIPES");
    }

    private void printGoodbye() {
        interactor.printWithBorderAndColor("GOOD BYE");
    }
}
