package de.qirdpdms.nivoraCoins.file;

import de.qirdpdms.nivoraCoins.EconomyPlugin;
import de.qirdpdms.nivoraCoins.util.ColorUtil;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {

    private final EconomyPlugin plugin;
    private final File messagesFile;
    private FileConfiguration messages;

    public MessageManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        reload();
    }

    public void reload() {
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        return getMessage(path, Collections.emptyMap(), true);
    }

    public String getMessage(String path, boolean withPrefix) {
        return getMessage(path, Collections.emptyMap(), withPrefix);
    }

    public String getMessage(String path, Map<String, String> placeholders, boolean withPrefix) {
        String message = messages.getString(path, "");
        if (message == null || message.isBlank()) {
            return "";
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        if (withPrefix) {
            message = getPrefix() + message;
        }

        return ColorUtil.color(message);
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Collections.emptyMap(), true);
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        send(sender, path, placeholders, true);
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders, boolean withPrefix) {
        String message = getMessage(path, placeholders, withPrefix);
        if (!message.isBlank()) {
            sender.sendMessage(message);
        }
    }

    public void log(String path) {
        log(path, Collections.emptyMap(), null);
    }

    public void log(String path, Map<String, String> placeholders) {
        log(path, placeholders, null);
    }

    public void logError(String path, Throwable throwable) {
        log(path, Collections.emptyMap(), throwable);
    }

    public void logError(String path, Map<String, String> placeholders, Throwable throwable) {
        log(path, placeholders, throwable);
    }

    private void log(String path, Map<String, String> placeholders, Throwable throwable) {
        String message = ColorUtil.strip(getMessage(path, placeholders, true));
        if (message.isBlank()) {
            return;
        }

        if (throwable == null) {
            plugin.getLogger().info(message);
            return;
        }

        plugin.getLogger().log(Level.SEVERE, message, throwable);
    }

    private String getPrefix() {
        return messages.getString("prefix", "");
    }
}


