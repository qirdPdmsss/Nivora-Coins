package de.qirdpdms.nivoraCoins.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class PlayerLookupUtil {

    private PlayerLookupUtil() {
    }

    public static OfflinePlayer findPlayer(String input) {
        Player onlinePlayer = Bukkit.getPlayerExact(input);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        OfflinePlayer cachedPlayer = Bukkit.getOfflinePlayerIfCached(input);
        if (cachedPlayer != null) {
            return cachedPlayer;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(input)) {
                return offlinePlayer;
            }
        }

        return null;
    }
}


