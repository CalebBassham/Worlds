package me.calebbassham.worlds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WorldCmd implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                String worldName = args[1];

                if (Bukkit.getWorld(worldName) != null) {
                    sender.sendMessage(worldName + " already exists.");
                    return true;
                }

                if (Worlds.worldDirectoryExists(worldName)) {
                    sender.sendMessage(worldName + " already exists but is not loaded.");
                    return true;
                }

                new WorldCreator(worldName).createWorld();
                Bukkit.broadcastMessage("Created " + worldName + ".");
            }

            if (args[0].equalsIgnoreCase("unload")) {
                String worldName = args[1];
                Bukkit.unloadWorld(worldName, true);
                Bukkit.broadcastMessage("Unloaded " + worldName + ".");
            }

            if (args[0].equalsIgnoreCase("tp")) {
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                World world = Bukkit.getWorld(args[1]);
                if (world == null) {
                    player.sendMessage(args[1] + " is not a loaded world.");
                    return true;
                }
                player.teleportAsync(world.getSpawnLocation());
            }

            if (args[0].equalsIgnoreCase("load")) {
                String worldName = args[1];

                if (Worlds.worldIsLoaded(worldName)) {
                    sender.sendMessage(worldName + " is already loaded.");
                    return true;
                }

                if (!Worlds.worldDirectoryExists(worldName)) {
                    sender.sendMessage(worldName + " does not exist.");
                    return true;
                }

                Bukkit.createWorld(WorldCreator.name(worldName));
            }

            if (args[0].equalsIgnoreCase("delete")) {
                String worldName = args[1];

                if (Worlds.worldIsLoaded(worldName)) {
                    sender.sendMessage(worldName + " is currently loaded.");
                    return true;
                }

                File worldDir = Worlds.getWorldDirectory(worldName);

                if (worldDir == null) {
                    sender.sendMessage(worldName + " does not exist.");
                    return true;
                }

                if(worldDir.delete()) {
                    sender.sendMessage(worldName + " has been deleted.");
                } else {
                    sender.sendMessage(worldName + " could not be deleted.");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("archive")) {
                String worldName = args[1];

                try {
                    Worlds.archiveWorld(worldName);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Error: " + e.getMessage());
                } catch (IOException e) {
                    sender.sendMessage("Failed to zip world folder.");
                    e.printStackTrace();
                }
            }

//            if (args[0].equalsIgnoreCase("generate")) {
//                String worldName = args[1];
//                File worldFolder = new File(worldName + File.separator + "region");
//                MCAWorld world = new MCAWorld(worldName, worldFolder, true);
//                EditSession session = new EditSessionBuilder(world)
//                        .checkMemory(false)
//                        .allowedRegionsEverywhere()
//                        .fastmode(true)
//                        .changeSetNull()
//                        .limitUnlimited()
//                        .build();
//                CuboidRegion region = new CuboidRegion(BlockVector3.at(-750, 0, -750), BlockVector3.at(750, 256, 750));
//                world.regenerate(region, session);
//                Bukkit.broadcastMessage("Generating (-750, 0, -750) to (750, 256, 750)");
//            }

        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
