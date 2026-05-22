package de.qirdpdms.nivoraCoins.command;

import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.util.PermissionUtil;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class EcoTabCompleter implements TabCompleter {

    private final ConfigManager configManager;

    public EcoTabCompleter(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PermissionUtil.COMMAND_ECO) && !sender.hasPermission(PermissionUtil.ADMIN)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return filter(configManager.getEcoActionSuggestions(), args[0]);
        }

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            return filter(configManager.getAmountSuggestions(), args[2]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> input, String search) {
        return input.stream()
                .filter(entry -> entry.toLowerCase(Locale.ROOT).startsWith(search.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}

