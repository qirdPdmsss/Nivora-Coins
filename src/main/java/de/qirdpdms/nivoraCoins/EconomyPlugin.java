package de.qirdpdms.nivoraCoins;

import de.qirdpdms.nivoraCoins.api.EconomyAPI;
import de.qirdpdms.nivoraCoins.cache.EconomyCache;
import de.qirdpdms.nivoraCoins.command.CommandRegistrar;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.listener.PlayerConnectionListener;
import de.qirdpdms.nivoraCoins.manager.DatabaseManager;
import de.qirdpdms.nivoraCoins.placeholder.CoinsPlaceholder;
import de.qirdpdms.nivoraCoins.provider.EconomyProvider;
import de.qirdpdms.nivoraCoins.repository.EconomyRepository;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private EconomyCache economyCache;
    private EconomyRepository economyRepository;
    private EconomyService economyService;
    private CommandRegistrar commandRegistrar;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.databaseManager = new DatabaseManager(configManager, messageManager);

        if (!databaseManager.connect()) {
            EconomyProvider.clear();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            databaseManager.createTable();
        } catch (IllegalStateException exception) {
            EconomyProvider.clear();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.economyCache = new EconomyCache();
        this.economyRepository = new EconomyRepository(databaseManager);
        this.economyService = new EconomyService(this, configManager, messageManager, economyRepository, economyCache);
        this.commandRegistrar = new CommandRegistrar(this, economyService, configManager, messageManager);

        EconomyProvider.set(economyService);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(economyService, messageManager), this);
        commandRegistrar.register();
        registerPlaceholder();
        messageManager.log("system.enabled");
    }

    @Override
    public void onDisable() {
        if (economyService != null) {
            economyService.saveAllAndClear();
        }

        EconomyProvider.clear();

        if (databaseManager != null) {
            databaseManager.close();
        }

        if (messageManager != null) {
            messageManager.log("system.disabled");
        }
    }

    private void registerPlaceholder() {
        if (!configManager.isPlaceholderEnabled()) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            messageManager.log("system.placeholder-unavailable");
            return;
        }

        new CoinsPlaceholder(this, economyService, configManager).register();
        messageManager.log("system.placeholder-registered");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyCache getEconomyCache() {
        return economyCache;
    }

    public EconomyRepository getEconomyRepository() {
        return economyRepository;
    }

    public EconomyAPI getEconomyApi() {
        return economyService;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }
}


