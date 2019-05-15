package com.faustas.dbms.services;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.utils.ConsoleColor;

import java.io.Console;
import java.util.Scanner;

@Service
public class ConsoleInteractor {
    private static final ConsoleColor DEFAULT_COLOR = ConsoleColor.CYAN;
    private static final ConsoleColor HEADER_COLOR = ConsoleColor.YELLOW;
    private static final ConsoleColor SUCCESS_COLOR = ConsoleColor.GREEN;
    private static final ConsoleColor SECTION_HEADER_COLOR = ConsoleColor.CYAN;
    private static final ConsoleColor ERROR_COLOR = ConsoleColor.RED;
    private static final int BORDER_LENGTH = 50;

    private Scanner scanner = new Scanner(System.in);

    public void print(String message) {
        System.out.println(message);
    }

    public String ask(String message) {
        print(message);
        return getString();
    }

    public String getString() {
        return getString("> ");
    }

    public String getString(String message) {
        System.out.print(message);
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

    public void printSeparator() {
        printSeparator('-');
    }

    public void printSeparator(char separatorChar) {
        print(getMultipleSymbol(separatorChar, BORDER_LENGTH));
    }

    public void printWithBorderAndColor(String message) {
        printWithBorderAndColor(message, DEFAULT_COLOR);
    }

    public void printWithBorderAndColor(String message, ConsoleColor color) {

        print(getMultipleSymbol('*', BORDER_LENGTH));
        printWithColor(centerMessage(message), color);
        print(getMultipleSymbol('*', BORDER_LENGTH));
    }

    public void printError(String error) {
        printWithColor(error, ERROR_COLOR);
    }

    public void printHeader(String header) {
        printWithBorderAndColor(header, HEADER_COLOR);
    }

    public void printSectionHeader(String sectionHeader) {
        printWithColor(sectionHeader, SECTION_HEADER_COLOR);
    }

    public void printWithColor(String message) {
        printWithColor(message, DEFAULT_COLOR);
    }

    public void printWithColor(String message, ConsoleColor color) {
        print(color.getColor() + message + ConsoleColor.RESET.getColor());
    }

    public void printCentered(String message) {
        print(centerMessage(message));
    }

    public void printSuccess(String message) {
        printWithColor(message, SUCCESS_COLOR);
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

    private String centerMessage(String message) {
        double paddingLength = (BORDER_LENGTH - message.length()) / 2;
        return getMultipleSymbol(' ', paddingLength) + message;
    }
}
