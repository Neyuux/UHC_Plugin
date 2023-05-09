package com.gmail.val59000mc.paperlib;

import com.gmail.val59000mc.paperlib.environments.CraftBukkitEnvironment;
import com.gmail.val59000mc.paperlib.environments.Environment;
import com.gmail.val59000mc.paperlib.environments.SpigotEnvironment;
import com.gmail.val59000mc.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class PaperLib {
    private static Environment ENVIRONMENT = new SpigotEnvironment();

    @Nonnull
    public static Environment getEnvironment() {
        return ENVIRONMENT;
    }

    public static void setCustomEnvironment(@Nonnull Environment environment) {
        ENVIRONMENT = environment;
    }

    @Nonnull
    public static CompletableFuture<Boolean> teleportAsync(@Nonnull Entity entity, @Nonnull Location location) {
        return ENVIRONMENT.teleport(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Nonnull
    public static CompletableFuture<Boolean> teleportAsync(@Nonnull Entity entity, @Nonnull Location location, PlayerTeleportEvent.TeleportCause cause) {
        return ENVIRONMENT.teleport(entity, location, cause);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull Location loc) {
        return getChunkAtAsync(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4, true);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull Location loc, boolean gen) {
        return getChunkAtAsync(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4, gen);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int x, int z) {
        return getChunkAtAsync(world, x, z, true);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int x, int z, boolean gen) {
        return ENVIRONMENT.getChunkAtAsync(world, x, z, gen, false);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int x, int z, boolean gen, boolean isUrgent) {
        return ENVIRONMENT.getChunkAtAsync(world, x, z, gen, isUrgent);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsyncUrgently(@Nonnull World world, int x, int z, boolean gen) {
        return ENVIRONMENT.getChunkAtAsync(world, x, z, gen, true);
    }

    public static boolean isChunkGenerated(@Nonnull Location loc) {
        return isChunkGenerated(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    public static boolean isChunkGenerated(@Nonnull World world, int x, int z) {
        return ENVIRONMENT.isChunkGenerated(world, x, z);
    }

    @Nonnull
    public static BlockStateSnapshotResult getBlockState(@Nonnull Block block, boolean useSnapshot) {
        return ENVIRONMENT.getBlockState(block, useSnapshot);
    }

    public static CompletableFuture<Location> getBedSpawnLocationAsync(@Nonnull Player player, boolean isUrgent) {
        return ENVIRONMENT.getBedSpawnLocationAsync(player, isUrgent);
    }

    public static boolean isVersion(int minor) {
        return ENVIRONMENT.isVersion(minor);
    }

    public static boolean isVersion(int minor, int patch) {
        return ENVIRONMENT.isVersion(minor, patch);
    }

    public static int getMinecraftVersion() {
        return ENVIRONMENT.getMinecraftVersion();
    }

    public static int getMinecraftPatchVersion() {
        return ENVIRONMENT.getMinecraftPatchVersion();
    }

    public static boolean isSpigot() {
        return ENVIRONMENT.isSpigot();
    }

    public static boolean isPaper() {
        return ENVIRONMENT.isPaper();
    }

    public static void suggestPaper(@Nonnull Plugin plugin) {
        if (isPaper())
            return;
        String benefitsProperty = "paperlib.shown-benefits";
        String pluginName = plugin.getDescription().getName();
        Logger logger = plugin.getLogger();
        logger.warning("====================================================");
        logger.warning(" " + pluginName + " works better if you use Paper ");
        logger.warning(" as your server software. ");
        if (System.getProperty("paperlib.shown-benefits") == null) {
            System.setProperty("paperlib.shown-benefits", "1");
            logger.warning("  ");
            logger.warning(" Paper offers significant performance improvements,");
            logger.warning(" bug fixes, security enhancements and optional");
            logger.warning(" features for server owners to enhance their server.");
            logger.warning("  ");
            logger.warning(" Paper includes Timings v2, which is significantly");
            logger.warning(" better at diagnosing lag problems over v1.");
            logger.warning("  ");
            logger.warning(" All of your plugins should still work, and the");
            logger.warning(" Paper community will gladly help you fix any issues.");
            logger.warning("  ");
            logger.warning(" Join the Paper Community @ https://papermc.io");
        }
        logger.warning("====================================================");
    }
}
