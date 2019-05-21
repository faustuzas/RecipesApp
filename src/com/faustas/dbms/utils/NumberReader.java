package com.faustas.dbms.utils;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.services.ConsoleInteractor;

@Service
public class NumberReader {

    private ConsoleInteractor interactor;

    public NumberReader(ConsoleInteractor interactor) {
        this.interactor = interactor;
    }

    public Integer readInteger(String message, String errorMessage) {
        while (true) {
            try {
                return Integer.valueOf(interactor.getString(message));
            } catch (NumberFormatException e) {
                interactor.printError(errorMessage);
            }
        }
    }

    public Double readDouble(String message, String errorMessage) {
        while (true) {
            try {
                return Double.valueOf(interactor.getString(message));
            } catch (NumberFormatException e) {
                interactor.printError(errorMessage);
            }
        }
    }
}
