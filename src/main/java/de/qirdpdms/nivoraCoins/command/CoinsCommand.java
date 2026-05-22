package de.qirdpdms.nivoraCoins.command;

import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import de.qirdpdms.nivoraCoins.util.PermissionUtil;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final EconomyService economyService;
    private final MessageManager messageManager;

    public CoinsCommand(ConfigManager configManager, EconomyService economyService, MessageManager messageManager) {
        this.configManager = configManager;
        this.economyService = economyService;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PermissionUtil.COMMAND_COINS) && !sender.hasPermission(PermissionUtil.ADMIN)) {
            messageManager.send(sender, "general.no-permission");
            return true;
        }

        if (!(sender instanceof Player player)) {
            messageManager.send(sender, "general.players-only");
            return true;
        }

        if (args.length != 0) {
            messageManager.send(sender, "commands.coins.usage", Map.of("usage", configManager.getCoinsCommandUsage()));
            return true;
        }

        messageManager.send(player, "commands.coins.balance", Map.of("value", economyService.formatCoins(player.getUniqueId())));
        return true;
    }
}


