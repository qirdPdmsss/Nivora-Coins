package de.qirdpdms.nivoraCoins.command;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.model.EconomyAction;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import de.qirdpdms.nivoraCoins.util.PermissionUtil;
import de.qirdpdms.nivoraCoins.util.PlayerLookupUtil;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {

    private final EconomyPlugin plugin;
    private final ConfigManager configManager;
    private final EconomyService economyService;
    private final MessageManager messageManager;

    public EcoCommand(EconomyPlugin plugin, ConfigManager configManager, EconomyService economyService, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.economyService = economyService;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PermissionUtil.COMMAND_ECO) && !sender.hasPermission(PermissionUtil.ADMIN)) {
            messageManager.send(sender, "general.no-permission");
            return true;
        }

        if (args.length != 3) {
            messageManager.send(sender, "commands.eco.usage", Map.of("usage", configManager.getEcoCommandUsage()));
            return true;
        }

        EconomyAction action = EconomyAction.fromInput(args[0]);
        if (action == null) {
            messageManager.send(sender, "commands.eco.invalid-action", Map.of("usage", configManager.getEcoCommandUsage()));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException exception) {
            messageManager.send(sender, "commands.eco.invalid-amount");
            return true;
        }

        if (amount <= 0L) {
            messageManager.send(sender, "commands.eco.amount-too-low");
            return true;
        }

        OfflinePlayer target = PlayerLookupUtil.findPlayer(args[1]);
        if (target == null || target.getUniqueId() == null) {
            messageManager.send(sender, "commands.eco.player-not-found", Map.of("player", args[1]));
            return true;
        }

        String targetName = target.getName() == null ? args[1] : target.getName();
        if (target.isOnline()) {
            executeAction(sender, action, target, targetName, amount);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            EcoExecutionResult result = executeAction(action, target, amount);
            Bukkit.getScheduler().runTask(plugin, () -> sendResult(sender, action, targetName, amount, result));
        });
        return true;
    }

    private void executeAction(CommandSender sender, EconomyAction action, OfflinePlayer target, String targetName, long amount) {
        EcoExecutionResult result = executeAction(action, target, amount);
        sendResult(sender, action, targetName, amount, result);
    }

    private EcoExecutionResult executeAction(EconomyAction action, OfflinePlayer target, long amount) {
        if (action == EconomyAction.REMOVE && !economyService.canRemoveCoins(target.getUniqueId(), amount)) {
            return new EcoExecutionResult(false, economyService.getCoins(target.getUniqueId()));
        }

        switch (action) {
            case ADD -> economyService.addCoins(target.getUniqueId(), amount);
            case SET -> economyService.setCoins(target.getUniqueId(), amount);
            case REMOVE -> economyService.removeCoins(target.getUniqueId(), amount);
        }

        return new EcoExecutionResult(true, economyService.getCoins(target.getUniqueId()));
    }

    private void sendResult(CommandSender sender, EconomyAction action, String targetName, long amount, EcoExecutionResult result) {
        String formattedAmount = economyService.formatCoins(amount);
        String formattedBalance = economyService.formatCoins(result.balance());
        Map<String, String> placeholders = Map.of(
                "player", targetName,
                "value", formattedAmount,
                "balance", formattedBalance
        );

        if (!result.success() && action == EconomyAction.REMOVE) {
            messageManager.send(sender, "commands.eco.remove-blocked", placeholders);
            return;
        }

        switch (action) {
            case ADD -> messageManager.send(sender, "commands.eco.add", placeholders);
            case SET -> messageManager.send(sender, "commands.eco.set", placeholders);
            case REMOVE -> messageManager.send(sender, "commands.eco.remove", placeholders);
        }
    }

    private record EcoExecutionResult(boolean success, long balance) {
    }
}


