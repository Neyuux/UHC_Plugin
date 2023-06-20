package fr.neyuux.uhc.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CorrectChunkGenerator {

    protected List<Block> getAdjacentsBlocks(Block startBlock, int nbrBlocks) {
        int failedAttempts = 0;
        List<Block> adjacentBlocks = new ArrayList<>();
        adjacentBlocks.add(startBlock);
        while (adjacentBlocks.size() < nbrBlocks && failedAttempts < 25) {
            Block block = adjacentBlocks.get(randomInteger(0, adjacentBlocks.size() - 1));
            BlockFace face = randomAdjacentFace();
            Location blockLocation = block.getLocation();
            if ((blockLocation.getBlockY() <= 1 && face.equals(BlockFace.DOWN)) || (blockLocation.getBlockY() >= 255 && face.equals(BlockFace.UP))) {
                failedAttempts++;
                continue;
            }
            Block adjacent = block.getRelative(face);
            if (adjacentBlocks.contains(adjacent) || !adjacent.getType().equals(Material.STONE)) {
                failedAttempts++;
                continue;
            }
            adjacentBlocks.add(adjacent);
        }
        return adjacentBlocks;
    }

    public abstract void generateInChunk(Chunk chunk);


    public static BlockFace randomAdjacentFace() {
        BlockFace[] faces = { BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
        return faces[new Random().nextInt(faces.length)];
    }

    public static int randomInteger(int min, int max) {
        Random r = new Random();
        int realMin = Math.min(min, max);
        int realMax = Math.max(min, max);
        int exclusiveSize = realMax - realMin;
        return r.nextInt(exclusiveSize + 1) + min;
    }
}
