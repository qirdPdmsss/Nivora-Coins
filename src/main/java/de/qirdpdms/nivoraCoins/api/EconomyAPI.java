package de.qirdpdms.nivoraCoins.api;

import java.util.UUID;

public interface EconomyAPI {

    long getCoins(UUID uuid);

    void addCoins(UUID uuid, long amount);

    void setCoins(UUID uuid, long amount);

    void removeCoins(UUID uuid, long amount);
}

