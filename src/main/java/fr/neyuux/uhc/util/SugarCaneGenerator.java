package fr.neyuux.uhc.util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SugarCaneGenerator extends CorrectChunkGenerator {

    @Override
    public void generateInChunk(Chunk chunk) {
        for (int i = 0; i < 20; i++) {
            int randNbrBlocks = randomInteger(1, 5);
            int randX = randomInteger(0, 15);
            int randZ = randomInteger(0, 15);
            Block randBlock = chunk.getBlock(randX, chunk.getWorld().getHighestBlockYAt(randX, randZ), randZ);
            Material type = randBlock.getType();

            if (this.checkWaterNearby(randBlock) && (type.equals(Material.DIRT) || type.equals(Material.SAND) || type.equals(Material.GRASS))) {
                for (int j = 1; j <= randNbrBlocks; j++) {
                    chunk.getBlock(randX, randBlock.getY() + j, randZ).setType(Material.SUGAR_CANE_BLOCK);
                }
            }
        }
    }

    public boolean checkWaterNearby(Block block) {
        BlockFace[] toCheckFaces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        for (BlockFace blockFace : toCheckFaces) {
            Material type =  block.getRelative(blockFace).getType();
            if (type.equals(Material.STATIONARY_WATER))
                return true;
        }

        return false;
    }

}
