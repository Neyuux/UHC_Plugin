package fr.neyuux.uhc.util;

import com.gmail.val59000mc.paperlib.PaperLib;
import fr.neyuux.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * @author Mezy & val59000
 */

public abstract class UHCChunkLoader implements Runnable {
    private final World world;

    private final int sleepEveryNumOfChunks;

    private final int sleepDuration;

    private final int maxChunk;

    private int x;

    private int z;

    private final int totalChunksToLoad;

    private int chunksLoaded;

    public UHCChunkLoader(World world, int size, int sleepEveryNumOfChunks, int sleepDuration) {
        this.world = world;
        this.sleepEveryNumOfChunks = sleepEveryNumOfChunks;
        this.sleepDuration = sleepDuration;
        this.maxChunk = Math.round(size / 16.0F) + 1;
        this.totalChunksToLoad = (2 * this.maxChunk + 1) * (2 * this.maxChunk + 1);
        this.x = -this.maxChunk;
        this.z = -this.maxChunk;
    }

    public void run() {
        int loaded = 0;
        while (this.x <= this.maxChunk && loaded < this.sleepEveryNumOfChunks) {
            try {
                Chunk chunk = PaperLib.getChunkAtAsync(this.world, this.x, this.z, true).get();
                if (Bukkit.isPrimaryThread()) {
                    this.onDoneLoadingChunk(chunk);
                } else {
                    Bukkit.getScheduler().runTask(UHC.getInstance(), () -> this.onDoneLoadingChunk(chunk));
                }
            } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
                e.printStackTrace();
            }
            loaded++;
            this.z++;
            if (this.z > this.maxChunk) {
                this.z = -this.maxChunk;
                this.x++;
            }
        }
        this.chunksLoaded += loaded;
        if (this.x <= this.maxChunk) {
            Bukkit.getLogger().info("UHC >> Loading map " + getLoadingState() + "% - " + this.chunksLoaded + "/" + this.totalChunksToLoad + " chunks loaded");
            UHC.sendActionBarForAllPlayers(UHC.getPrefix() + "§2Préchargement du monde §a§l" + getLoadingState() + "%§2 - §a" + this.chunksLoaded + "§2/§a" + this.totalChunksToLoad + " §2chunks loaded");
            Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getInstance(), this, this.sleepDuration);
        } else {
            Bukkit.getScheduler().runTask(UHC.getInstance(), this::onDoneLoadingWorld);
        }
    }

    public void printSettings() {
        Bukkit.getLogger().info("UHC >> Generating environment " + this.world.getEnvironment().toString());
        Bukkit.getLogger().info("UHC >> Loading a total " + Math.floor(this.totalChunksToLoad) + " chunks, up to chunk ( " + this.maxChunk + " , " + this.maxChunk + " )");
        Bukkit.getLogger().info("UHC >> Sleeping " + this.sleepDuration + " ticks every " + this.sleepEveryNumOfChunks + " chunks");
        Bukkit.getLogger().info("UHC >> Loading map " + getLoadingState() + "%");
    }

    private String getLoadingState() {
        double percentage = 100.0D * this.chunksLoaded / this.totalChunksToLoad;
        return this.world.getEnvironment() + " " + (Math.floor(10.0D * percentage) / 10.0D);
    }

    public abstract void onDoneLoadingWorld();

    public abstract void onDoneLoadingChunk(Chunk paramChunk);
}