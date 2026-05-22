package de.qirdpdms.nivoraCoins.listener;

import de.qirdpdms.nivoraCoins.file.MessageManager;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import java.sql.SQLException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final EconomyService economyService;
    private final MessageManager messageManager;

    public PlayerConnectionListener(EconomyService economyService, MessageManager messageManager) {
        this.economyService = economyService;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            economyService.preloadPlayer(event.getUniqueId());
        } catch (SQLException exception) {
            messageManager.logError("system.database-load-failed", exception);
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    LegacyComponentSerializer.legacyAmpersand().deserialize(messageManager.getMessage("database.login-failed"))
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        economyService.saveAndUnloadAsync(event.getPlayer().getUniqueId());
    }
}



