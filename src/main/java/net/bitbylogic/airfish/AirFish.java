package net.bitbylogic.airfish;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class AirFish extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new FishListener(this), this);

        new Metrics(this, 22124);
    }

    @Override
    public void onDisable() {

    }

}
