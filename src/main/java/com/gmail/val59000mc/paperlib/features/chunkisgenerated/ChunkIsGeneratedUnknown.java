package com.gmail.val59000mc.paperlib.features.chunkisgenerated;

import org.bukkit.World;

public class ChunkIsGeneratedUnknown implements ChunkIsGenerated {
    public boolean isChunkGenerated(World world, int x, int z) {
        return true;
    }
}
