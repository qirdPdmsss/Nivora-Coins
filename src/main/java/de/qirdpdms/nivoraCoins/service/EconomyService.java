package de.qirdpdms.nivoraCoins.service;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import de.qirdpdms.nivoraCoins.api.EconomyAPI;
import de.qirdpdms.nivoraCoins.cache.EconomyCache;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.repository.EconomyRepository;
import de.qirdpdms.nivoraCoins.util.NumberFormatter;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;

public class EconomyService implements EconomyAPI {

    private final EconomyPlugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final EconomyRepository economyRepository;
    private final EconomyCache economyCache;

    public EconomyService(EconomyPlugin plugin, ConfigManager configManager, MessageManager messageManager, EconomyRepository economyRepository, EconomyCache economyCache) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.economyRepository = economyRepository;
        this.economyCache = economyCache;
    }

    @Override
    public long getCoins(UUID uuid) {
        Long cachedCoins = economyCache.getIfPresent(uuid);
        if (cachedCoins != null) {
            return cachedCoins;
        }

        try {
            long coins = normalize(economyRepository.loadCoins(uuid));
            economyCache.put(uuid, coins);
            return coins;
        } catch (SQLException exception) {
            messageManager.logError("system.database-load-failed", exception);
            return getLowestPossibleBalance();
        }
    }

    @Override
    public void addCoins(UUID uuid, long amount) {
        long updatedCoins = normalize(addBounded(getCoins(uuid), amount));
        updateCoins(uuid, updatedCoins);
    }

    @Override
    public void setCoins(UUID uuid, long amount) {
        long normalizedCoins = normalize(amount);
        updateCoins(uuid, normalizedCoins);
    }

    @Override
    public void removeCoins(UUID uuid, long amount) {
        long currentCoins = getCoins(uuid);
        if (!canRemoveCoins(currentCoins, amount)) {
            return;
        }

        long updatedCoins = normalize(subtractBounded(currentCoins, amount));
        updateCoins(uuid, updatedCoins);
    }

    public boolean canRemoveCoins(UUID uuid, long amount) {
        return canRemoveCoins(getCoins(uuid), amount);
    }

    public boolean canRemoveCoins(long currentCoins, long amount) {
        if (amount < 0L) {
            return true;
        }

        if (configManager.isNegativeBalanceAllowed()) {
            return true;
        }

        return subtractBounded(currentCoins, amount) >= getLowestPossibleBalance();
    }

    public void preloadPlayer(UUID uuid) throws SQLException {
        long coins = normalize(economyRepository.loadCoins(uuid));
        economyCache.put(uuid, coins);
    }

    public void saveAndUnloadAsync(UUID uuid) {
        Long cachedCoins = economyCache.getIfPresent(uuid);
        if (cachedCoins == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                economyRepository.saveCoins(uuid, cachedCoins);
                economyCache.remove(uuid);
            } catch (SQLException exception) {
                messageManager.logError("system.database-save-failed", exception);
            }
        });
    }

    public void saveAllAndClear() {
        for (Map.Entry<UUID, Long> entry : economyCache.snapshot().entrySet()) {
            try {
                economyRepository.saveCoins(entry.getKey(), entry.getValue());
                economyCache.remove(entry.getKey());
            } catch (SQLException exception) {
                messageManager.logError("system.database-save-failed", exception);
            }
        }
    }

    public String formatCoins(long value) {
        return NumberFormatter.format(value, configManager.getFormattingLocale());
    }

    public String formatCoins(UUID uuid) {
        return formatCoins(getCoins(uuid));
    }

    private void updateCoins(UUID uuid, long coins) {
        economyCache.put(uuid, coins);
        if (Bukkit.getPlayer(uuid) == null) {
            try {
                economyRepository.saveCoins(uuid, coins);
                economyCache.remove(uuid);
            } catch (SQLException exception) {
                messageManager.logError("system.database-save-failed", exception);
            }
        }
    }

    private long normalize(long coins) {
        return Math.max(coins, getLowestPossibleBalance());
    }

    private long getLowestPossibleBalance() {
        return configManager.isNegativeBalanceAllowed() ? configManager.getMinimumBalance() : Math.max(0L, configManager.getMinimumBalance());
    }

    private long addBounded(long left, long right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException exception) {
            return right >= 0L ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
    }

    private long subtractBounded(long left, long right) {
        try {
            return Math.subtractExact(left, right);
        } catch (ArithmeticException exception) {
            return right >= 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }
}


