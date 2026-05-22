package de.qirdpdms.nivoraCoins.provider;

import de.qirdpdms.nivoraCoins.api.EconomyAPI;

public final class EconomyProvider {

    private static EconomyAPI instance;

    private EconomyProvider() {
    }

    public static EconomyAPI get() {
        if (instance == null) {
            throw new IllegalStateException();
        }

        return instance;
    }

    public static void set(EconomyAPI economyAPI) {
        instance = economyAPI;
    }

    public static void clear() {
        instance = null;
    }
}


