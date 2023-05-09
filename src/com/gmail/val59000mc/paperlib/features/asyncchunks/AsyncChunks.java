package com.gmail.val59000mc.paperlib.features.asyncchunks;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public interface AsyncChunks {
    default CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
        return getChunkAtAsync(world, x, z, gen, false);
    }

    CompletableFuture<Chunk> getChunkAtAsync(World paramWorld, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
}