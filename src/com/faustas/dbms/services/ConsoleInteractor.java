package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.utils.ConsoleColor;

import java.io.Console;
import java.util.Scanner;

@Service
public class ConsoleInteractor {
    private static final ConsoleColor DEFAULT_COLOR = ConsoleColor.CYAN;
    private static final ConsoleColor ERROR_COLOR = ConsoleColor.RED;

    private Scanner scanner = new Scanner(System.in);

    public void print(String message) {
        System.out.println(message);
    }

    public String getString() {
        System.out.print("> ");
        return scanner.nextLine();
    }

    public String getPassword() {
        Console console = System.console();
        char[] passwordArray;
        if (console == null) {
            print("Password masking will not work in the IDE");
            System.out.print("> ");
            passwordArray = scanner.nextLine().toCharArray();
        } else {
            passwordArray = console.readPassword("> ");
        }

        return new String(passwordArray);
    }

    public void printWithBorderAndColor(String message) {
        printWithBorderAndColor(message, DEFAULT_COLOR);
    }

    public void printWithBorderAndColor(String message, ConsoleColor color) {
        double borderLength = message.length() * 1.5;
        double paddingLength = (borderLength - message.length()) / 2;

        print(getMultipleSymbol('*', borderLength));
        printWithColor(getMultipleSymbol(' ', paddingLength) + message, color);
        print(getMultipleSymbol('*', borderLength));
    }

    public void printError(String error) {
        printWithColor(error, ERROR_COLOR);
    }

    public void printWithColor(String message) {
        printWithColor(message, DEFAULT_COLOR);
    }

    public void printWithColor(String message, ConsoleColor color) {
        print(color.getColor() + message + ConsoleColor.RESET.getColor());
    }

    private String getMultipleSymbol(char c, double count) {
        return getMultipleSymbol(c, (int)Math.round(count));
    }

    private String getMultipleSymbol(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(c);
        }
        return sb.toString();
    }
}
