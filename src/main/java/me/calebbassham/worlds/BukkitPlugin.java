package me.calebbassham.worlds;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommand("world", new WorldCmd());
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand pCmd = getCommand(name);
        pCmd.setExecutor(executor);
        if (executor instanceof TabCompleter) {
            pCmd.setTabCompleter((TabCompleter) executor);
        }
    }
}
