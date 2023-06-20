package com.gmail.val59000mc.paperlib.features.blockstatesnapshot;

import org.bukkit.block.Block;

public class BlockStateSnapshotBeforeSnapshots implements BlockStateSnapshot {
    public BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot) {
        return new BlockStateSnapshotResult(false, block.getState());
    }
}
