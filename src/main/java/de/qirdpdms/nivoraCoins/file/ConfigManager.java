package de.qirdpdms.nivoraCoins.file;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import java.io.File;
import java.util.List;
import java.util.Locale;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final EconomyPlugin plugin;

    public ConfigManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        saveDefaults();
    }

    private void saveDefaults() {
        plugin.saveDefaultConfig();
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public String getDatabaseHost() {
        return getConfig().getString("database.host", "127.0.0.1");
    }

    public int getDatabasePort() {
        return getConfig().getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return getConfig().getString("database.name", "nivora");
    }

    public String getDatabaseUsername() {
        return getConfig().getString("database.username", "root");
    }

    public String getDatabasePassword() {
        return getConfig().getString("database.password", "password");
    }

    public String getTableName() {
        return getConfig().getString("database.table-name", "coins");
    }

    public String getPoolName() {
        return getConfig().getString("database.pool-name", "NivoraCoinsPool");
    }

    public int getPoolSize() {
        return getConfig().getInt("database.pool-size", 10);
    }

    public int getMinimumIdle() {
        return getConfig().getInt("database.minimum-idle", 2);
    }

    public long getConnectionTimeout() {
        return getConfig().getLong("database.connection-timeout", 10000L);
    }

    public long getMaxLifetime() {
        return getConfig().getLong("database.max-lifetime", 1800000L);
    }

    public long getKeepaliveTime() {
        return getConfig().getLong("database.keepalive-time", 300000L);
    }

    public long getLeakDetectionThreshold() {
        return getConfig().getLong("database.leak-detection-threshold", 0L);
    }

    public boolean isNegativeBalanceAllowed() {
        return getConfig().getBoolean("economy.allow-negative-balances", false);
    }

    public long getMinimumBalance() {
        return getConfig().getLong("economy.minimum-balance", 0L);
    }

    public Locale getFormattingLocale() {
        return Locale.forLanguageTag(getConfig().getString("format.locale", "de-DE"));
    }

    public boolean isPlaceholderEnabled() {
        return getConfig().getBoolean("placeholder.enabled", true);
    }

    public String getCoinsCommandUsage() {
        return getConfig().getString("commands.coins.usage", "/coins");
    }

    public String getEcoCommandUsage() {
        return getConfig().getString("commands.eco.usage", "/eco <add|set|remove> <player> <amount>");
    }

    public List<String> getEcoActionSuggestions() {
        return getConfig().getStringList("suggestions.eco-actions");
    }

    public List<String> getAmountSuggestions() {
        return getConfig().getStringList("suggestions.amounts");
    }
}


