package de.qirdpdms.nivoraCoins.placeholder;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import de.qirdpdms.nivoraCoins.file.ConfigManager;
import de.qirdpdms.nivoraCoins.service.EconomyService;
import de.qirdpdms.nivoraCoins.util.PermissionUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoinsPlaceholder extends PlaceholderExpansion {

    private final EconomyPlugin plugin;
    private final EconomyService economyService;
    private final ConfigManager configManager;

    public CoinsPlaceholder(EconomyPlugin plugin, EconomyService economyService, ConfigManager configManager) {
        this.plugin = plugin;
        this.economyService = economyService;
        this.configManager = configManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "coins";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().isEmpty() ? plugin.getName() : String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null || !player.hasPermission(PermissionUtil.FEATURE_PLACEHOLDER)) {
            return "";
        }

        if (params.isBlank() || params.equalsIgnoreCase("formatted")) {
            return economyService.formatCoins(player.getUniqueId());
        }

        if (params.equalsIgnoreCase("raw")) {
            return String.valueOf(economyService.getCoins(player.getUniqueId()));
        }

        if (params.equalsIgnoreCase("locale")) {
            return configManager.getFormattingLocale().toLanguageTag();
        }

        return "";
    }
}


