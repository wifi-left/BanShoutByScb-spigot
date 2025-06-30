package io.wifi.cmdshout;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    final static String SCORE_NAME = "BAMBOO_MOD_SAYING";

    @Override
    public void onEnable() {
        this.getCommand("sshout").setExecutor(new CommandSshout());
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        if (this.getServer().getScoreboardManager().getMainScoreboard().getObjective(SCORE_NAME) == null) {
            this.getServer().getScoreboardManager().getMainScoreboard().registerNewObjective(SCORE_NAME, "dummy",
                    SCORE_NAME);
            getLogger().info("Register New Objective [" + SCORE_NAME + "]");
        }
        getLogger().info("wifi-left's plugin [Speech Manager by Command 'scoreboard'] has loaded!");
    }

    @Override
    public void onDisable() {
    }
}