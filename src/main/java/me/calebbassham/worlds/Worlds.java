package me.calebbassham.worlds;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class Worlds {

    public static File getWorldDirectory(String worldName) {
        File worldDir = Bukkit.getWorldContainer().toPath().resolve(worldName).toFile();
        if (worldDir == null || !worldDir.exists()) return null;
        if (!worldDir.isDirectory()) return null;
        return worldDir;
    }

    public static boolean worldDirectoryExists(String worldName) {
        return getWorldDirectory(worldName) != null;
    }

    public static boolean worldIsLoaded(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }

    /**
     * Converts a world directory into a zip
     * @throws IllegalStateException When the world is loaded or does not have a world folder.
     * @throws IOException When the world folder fails to zip.
     */
    public static void archiveWorld(String worldName) throws IllegalArgumentException, IOException {
        if (worldIsLoaded(worldName)) {
            throw new IllegalStateException(worldName + " must be unloaded before it can be archived.");
        }

        File world = getWorldDirectory(worldName);

        if (world == null) {
            throw new IllegalStateException(worldName + " does not exist.");
        }

        Util.zip(world.toPath(), Bukkit.getWorldContainer().toPath().resolve(worldName + ".zip"));
    }

}