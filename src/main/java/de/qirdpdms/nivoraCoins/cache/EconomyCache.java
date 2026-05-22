package de.qirdpdms.nivoraCoins.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyCache {

    private final Map<UUID, Long> cache = new ConcurrentHashMap<>();

    public Long getIfPresent(UUID uuid) {
        return cache.get(uuid);
    }

    public void put(UUID uuid, long coins) {
        cache.put(uuid, coins);
    }

    public void remove(UUID uuid) {
        cache.remove(uuid);
    }

    public Map<UUID, Long> snapshot() {
        return Map.copyOf(cache);
    }
}


