package fr.neyuux.uhc.util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class VeinGenerator extends CorrectChunkGenerator {

    public void generateInChunk(Chunk chunk) {
        this.generateForOre(chunk, Material.DIAMOND_ORE, 1, 9, 3, 15);
        this.generateForOre(chunk, Material.GOLD_ORE, 1, 10, 3, 30);
    }

    private void generateVein(Material material, Block startBlock, int nbrBlocks) {
        List<Block> blocks = getAdjacentsBlocks(startBlock, nbrBlocks);
        for (Block block : blocks)
            block.setType(material);
    }

    public void generateForOre(Chunk chunk, Material material, int minBlocksPerVein, int maxBlocksPerVein, int minY, int maxY) {
        for (int i = 0; i < getNumberOfVeinsByProbability(material); i++) {
            int randNbrBlocks = randomInteger(minBlocksPerVein, maxBlocksPerVein);
            if (randNbrBlocks > 0) {
                Block randBlock = tryAdjustingToProperBlock(chunk.getBlock(randomInteger(0, 15), randomInteger(minY, maxY), randomInteger(0, 15)));
                if (randBlock != null)
                    generateVein(material, randBlock, randNbrBlocks);
            }
        }
    }

    private Block tryAdjustingToProperBlock(Block randBlock) {
        if (randBlock.getType().equals(Material.STONE))
            return randBlock;
        if (randBlock.getType().equals(Material.STATIONARY_WATER)) {
            while (randBlock.getType().equals(Material.STATIONARY_WATER) && randBlock.getY() > 10)
                randBlock = randBlock.getRelative(0, -10, 0);
            if (randBlock.getType().equals(Material.STONE))
                return randBlock;
        }
        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                for (int k = -5; k <= 5; k++) {
                    Block relativeBlock = randBlock.getRelative(i, j, k);
                    if (relativeBlock.getType().equals(Material.STONE))
                        return relativeBlock;
                }
            }
        }
        return null;
    }

    private int getNumberOfVeinsByProbability(Material material) {
        int i = randomInteger(0, 100);

        if (material.equals(Material.DIAMOND_ORE)) {
            if (i < 55) {
                return 0;
            } else if (i < 90) {
                return 1;
            } else return 2;

        } else if (material.equals(Material.GOLD_ORE)) {
            if (i < 29)
                return 0;
            else if (i < 55)
                return 1;
            else if (i < 95)
                return 2;
            else
                return 3;
        }
        return 0;
    }
}
