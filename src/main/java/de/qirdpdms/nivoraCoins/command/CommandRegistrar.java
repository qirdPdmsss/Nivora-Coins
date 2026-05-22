package de.qirdpdms.nivoraCoins.command;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import java.util.Map;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar {

    private final EconomyPlugin plugin;
    private final EconomyService economyService;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public CommandRegistrar(EconomyPlugin plugin, EconomyService economyService, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.economyService = economyService;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    public void register() {
        registerCoinsCommand();
        registerEcoCommand();
    }

    private void registerCoinsCommand() {
        PluginCommand command = plugin.getCommand("coins");
        if (command == null) {
            messageManager.log("system.command-registration-failed", Map.of("command", "coins"));
            return;
        }

        command.setExecutor(new CoinsCommand(configManager, economyService, messageManager));
        command.setTabCompleter(new CoinsTabCompleter());
    }

    private void registerEcoCommand() {
        PluginCommand command = plugin.getCommand("eco");
        if (command == null) {
            messageManager.log("system.command-registration-failed", Map.of("command", "eco"));
            return;
        }

        command.setExecutor(new EcoCommand(plugin, configManager, economyService, messageManager));
        command.setTabCompleter(new EcoTabCompleter(configManager));
    }
}


