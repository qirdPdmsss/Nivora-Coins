package de.qirdpdms.nivoraCoins.model;

import java.util.Arrays;

public enum EconomyAction {

    ADD("add"),
    SET("set"),
    REMOVE("remove");

    private final String input;

    EconomyAction(String input) {
        this.input = input;
    }

    public static EconomyAction fromInput(String input) {
        return Arrays.stream(values())
                .filter(action -> action.input.equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);
    }
}


