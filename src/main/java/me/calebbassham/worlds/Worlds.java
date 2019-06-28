package me.calebbassham.worlds;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Worlds {

    public static File getDirectoryInWorldsDirectory(String worldName) {
        File worldDir = Bukkit.getWorldContainer().toPath().resolve(worldName).toFile();
        if (worldDir == null || !worldDir.exists()) return null;
        if (!worldDir.isDirectory()) return null;
        return worldDir;
    }

    public static boolean worldDirectoryExists(String worldName) {
        return getDirectoryInWorldsDirectory(worldName) != null;
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

        File world = getDirectoryInWorldsDirectory(worldName);

        if (world == null) {
            throw new IllegalStateException(worldName + " does not exist.");
        }

        Util.zip(world.toPath(), Bukkit.getWorldContainer().toPath().resolve(worldName + ".zip"));
    }

    public static boolean isWorldDirectory(File file) {
        File regionDir = file.toPath().resolve("region").toFile();
        return regionDir != null && regionDir.exists() && regionDir.isDirectory();
    }

    public static Set<File> getWorldDirectories() {
        return Arrays.stream(Bukkit.getWorldContainer().listFiles())
                .filter(Worlds::isWorldDirectory)
                .collect(Collectors.toSet());
    }

}
