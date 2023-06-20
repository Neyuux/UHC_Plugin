package com.gmail.val59000mc.paperlib.features.blockstatesnapshot;

import org.bukkit.block.Block;

public interface BlockStateSnapshot {
    BlockStateSnapshotResult getBlockState(Block paramBlock, boolean paramBoolean);
}
