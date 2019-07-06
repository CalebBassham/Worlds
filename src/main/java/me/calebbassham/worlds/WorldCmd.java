package me.calebbassham.worlds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static me.calebbassham.pluginmessageformat.PluginMessageFormat.*;

public class WorldCmd implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getErrorPrefix() + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            sender.sendMessage(getPrefix() + "You are in " + getMainColorPallet().getHighlightTextColor() + player.getWorld().getName() + getMainColorPallet().getPrimaryTextColor() + ".");
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                String worldName = args[1];

                if (Bukkit.getWorld(worldName) != null) {
                    sender.sendMessage(worldName + " already exists.");
                    return true;
                }

                File worldDir = Worlds.getDirectoryInWorldsDirectory(worldName);

                if (worldDir != null) {
                    if (!Worlds.isWorldDirectory(worldDir)) {
                        sender.sendMessage("Cannot create " + worldName + " world because the " + worldName + " directory already exists.");
                        return true;
                    }

                    sender.sendMessage(worldName + " already exists but is not loaded.");
                    return true;
                }

                sender.sendMessage(getPrefix() + "Started to create " + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + ".");

                WorldCreator wc = new WorldCreator(worldName);
                if (worldName.endsWith("_nether")) {
                    wc.environment(World.Environment.NETHER);
                }

                if (worldName.endsWith("_the_end")) {
                    wc.environment(World.Environment.THE_END);
                }
                wc.createWorld();

                sender.sendMessage(getPrefix() + "Finished creating " + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + ".");
                return true;
            }

            if (args[0].equalsIgnoreCase("unload")) {
                String worldName = args[1];
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " does not exist.");
                    return true;
                }

                if (world.getPlayerCount() > 0) {
                    sender.sendMessage(getErrorPrefix() + "Cannot unload world while players are in it.");
                    return true;
                }

                Bukkit.unloadWorld(worldName, true);
                Bukkit.broadcastMessage(getPrefix() + "Unloaded " + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + ".");
                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                World world = Bukkit.getWorld(args[1]);
                if (world == null) {
                    player.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + args[1] + getErrorColorPallet().getPrimaryTextColor() + " is not a loaded world.");
                    return true;
                }
                player.teleportAsync(world.getSpawnLocation());
            }

            if (args[0].equalsIgnoreCase("load")) {
                String worldName = args[1];

                if (Worlds.worldIsLoaded(worldName)) {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " is already loaded.");
                    return true;
                }

                if (!Worlds.worldDirectoryExists(worldName)) {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " does not exist.");
                    return true;
                }

                Bukkit.createWorld(WorldCreator.name(worldName));
                sender.sendMessage(getPrefix() + "Loaded " + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + ".");
            }

            if (args[0].equalsIgnoreCase("delete")) {
                String worldName = args[1];

                if (Worlds.worldIsLoaded(worldName)) {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " is currently loaded.");
                    return true;
                }

                File worldDir = Worlds.getDirectoryInWorldsDirectory(worldName);

                if (worldDir == null) {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " does not exist.");
                    return true;
                }

                if (Util.deleteDirectory(worldDir)) {
                    sender.sendMessage(getPrefix() + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + " has been deleted.");
                } else {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " could not be deleted.");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("archive")) {
                String worldName = args[1];

                try {
                    Worlds.archiveWorld(worldName);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(getErrorPrefix() + e.getMessage());
                    return true;
                } catch (IOException e) {
                    sender.sendMessage(getErrorPrefix() + "Failed to zip world folder.");
                    e.printStackTrace();
                    return true;
                }

                sender.sendMessage(getPrefix() + "Archived " + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + ".");

                if(Util.deleteDirectory(Worlds.getDirectoryInWorldsDirectory(worldName))) {
                    sender.sendMessage(getPrefix() + getMainColorPallet().getValueTextColor() + worldName + getMainColorPallet().getPrimaryTextColor() + " has been deleted.");
                } else {
                    sender.sendMessage(getErrorPrefix() + getErrorColorPallet().getValueTextColor() + worldName + getErrorColorPallet().getPrimaryTextColor() + " could not be deleted.");
                }

                return true;
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

        if (args.length == 1) {
            if (args[0].equals("list")) {

                sender.sendMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Worlds" + getMainColorPallet().getExtraTextColor() + ":");

                Set<String> worldNames = new HashSet<>();
                worldNames.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toSet()));
                worldNames.addAll(Worlds.getWorldDirectories().stream().map(File::getName).collect(Collectors.toSet()));

                worldNames.stream()
                        .sorted((world1, world2) -> Boolean.compare(Bukkit.getWorld(world1) == null, Bukkit.getWorld(world2) == null))
                        .forEach(world -> {
                            boolean loaded = Bukkit.getWorld(world) != null;
                            sender.sendMessage(getMainColorPallet().getExtraTextColor() + "    - " + getMainColorPallet().getPrimaryTextColor() + world + getMainColorPallet().getExtraTextColor() + ChatColor.ITALIC + (!loaded ? " (unloaded)" : ""));
                        });
                return true;
            }
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(new String[]{"create", "unload", "tp", "load", "delete", "archive", "list"})
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("tp")) {
                return Bukkit.getWorlds().stream()
                        .map(World::getName)
                        .filter(w -> w.toLowerCase().startsWith(args[1].toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
            }

            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("archive") || args[0].equalsIgnoreCase("load")) {
                return Worlds.getUnloadedWorlds()
                        .stream()
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
