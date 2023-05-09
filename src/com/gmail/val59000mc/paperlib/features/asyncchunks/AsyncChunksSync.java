package com.gmail.val59000mc.paperlib.features.asyncchunks;

import com.gmail.val59000mc.paperlib.PaperLib;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksSync implements AsyncChunks {
    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
        if (!gen && !PaperLib.isChunkGenerated(world, x, z))
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(world.getChunkAt(x, z));
    }
}
