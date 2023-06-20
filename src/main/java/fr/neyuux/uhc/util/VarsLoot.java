package fr.neyuux.uhc.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class VarsLoot {

    private static HashMap<Material, Loot> blocksLoots = new HashMap<>();
    private static HashMap<EntityType, Loot> entitiesLoots = new HashMap<>();


    public static HashMap<Material, Loot> getBlocksLoots() {
        return blocksLoots;
    }

    public static void setBlocksLoots(HashMap<Material, Loot> loots) {
        blocksLoots = loots;
    }

    public static HashMap<EntityType, Loot> getEntitiesLoots() {
        return entitiesLoots;
    }

    public static void setEntitiesLoots(HashMap<EntityType, Loot> loots) {
        entitiesLoots = loots;
    }
}

