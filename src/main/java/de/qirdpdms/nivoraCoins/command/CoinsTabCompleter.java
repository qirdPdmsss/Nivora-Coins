package de.qirdpdms.nivoraCoins.command;

import de.qirdpdms.nivoraCoins.util.PermissionUtil;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CoinsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PermissionUtil.COMMAND_COINS) && !sender.hasPermission(PermissionUtil.ADMIN)) {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}

