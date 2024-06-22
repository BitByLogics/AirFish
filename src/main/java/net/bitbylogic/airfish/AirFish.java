package net.bitbylogic.airfish;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AirFish extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new FishListener(this), this);

        new Metrics(this, 22124);

        new UpdateChecker(this, UpdateCheckSource.SPIGOT, "117093")
                .setNotifyRequesters(false)
                .setNotifyOpsOnJoin(false)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent())
                .checkEveryXHours(12)
                .onSuccess((commandSenders, latestVersion) -> {
                    String messagePrefix = "&8[&6Air Fish&8] ";
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.equalsIgnoreCase(latestVersion)) {
                        String updateMessage = color(messagePrefix + "&aYou are using the latest version of AirFish!");

                        Bukkit.getConsoleSender().sendMessage(updateMessage);
                        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessage));
                        return;
                    }

                    List<String> updateMessages = List.of(
                            color(messagePrefix + "&cYour version of AirFish is outdated!"),
                            color(String.format(messagePrefix + "&cYou are using %s, latest is %s!", currentVersion, latestVersion)),
                            color(messagePrefix + "&cDownload latest here:"),
                            color("&6https://www.spigotmc.org/resources/airfish.117093/")
                    );

                    Bukkit.getConsoleSender().sendMessage(updateMessages.toArray(new String[]{}));
                    Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessages.toArray(new String[]{})));
                })
                .onFail((commandSenders, e) -> {}).checkNow();
    }

    @Override
    public void onDisable() {

    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
