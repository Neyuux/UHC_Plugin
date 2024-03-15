package fr.neyuux.uhc.util.nms;

import fr.neyuux.uhc.UHC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;
import org.spigotmc.SpigotWorldConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Maygo
 * @version 1.8
 */
public class NMSPatcher {

    /**
     * Permet d'ajouter plus d'animaux dans la nature, d'enlever les biomes inutiles et d'ajouter de la canne à sucre aux biomes
     *
     * @throws Exception Si le patch rencontre une erreur
     */
    public void patchBiomes() throws Exception {
        Bukkit.getLogger().info("Fix des biomes");

        this.fixAnimals();

        BiomeBase[] biomes = BiomeBase.getBiomes();
        biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
        biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.FOREST;
        biomes[BiomeBase.OCEAN.id] = BiomeBase.FOREST;
        biomes[BiomeBase.JUNGLE.id] = BiomeBase.FOREST;
        biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.FOREST;
        biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.DESERT;
        biomes[BiomeBase.MEGA_TAIGA.id] = BiomeBase.BIRCH_FOREST;
        biomes[BiomeBase.MEGA_TAIGA_HILLS.id] = BiomeBase.BEACH;
        biomes[BiomeBase.MESA.id] = BiomeBase.PLAINS;
        biomes[BiomeBase.MESA_PLATEAU.id] = BiomeBase.PLAINS;
        biomes[BiomeBase.MESA_PLATEAU_F.id] = BiomeBase.BIRCH_FOREST_HILLS;
        biomes[BiomeBase.ICE_PLAINS.id] = BiomeBase.PLAINS;
        biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.ROOFED_FOREST;
        biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.ROOFED_FOREST;
        biomes[BiomeBase.SWAMPLAND.id] = BiomeBase.PLAINS;
        Reflection.setFinalStatic(BiomeBase.class.getDeclaredField("biomes"), biomes);
    }

    private void fixAnimals() throws ReflectiveOperationException, IOException {
        Bukkit.getLogger().info("Fix des animaux dans les biomes");

        this.addAnimalsSpawn("PLAINS", BiomeBase.PLAINS);
        this.addAnimalsSpawn("DESERT", BiomeBase.DESERT);
        this.addAnimalsSpawn("EXTREME_HILLS", BiomeBase.EXTREME_HILLS);
        this.addAnimalsSpawn("FOREST", BiomeBase.FOREST);
        this.addAnimalsSpawn("TAIGA", BiomeBase.TAIGA);
        this.addAnimalsSpawn("SWAMPLAND", BiomeBase.SWAMPLAND);
        this.addAnimalsSpawn("RIVER", BiomeBase.RIVER);
        this.addAnimalsSpawn("FROZEN_OCEAN", BiomeBase.FROZEN_OCEAN);
        this.addAnimalsSpawn("FROZEN_RIVER", BiomeBase.FROZEN_RIVER);
        this.addAnimalsSpawn("MUSHROOM_ISLAND", BiomeBase.MUSHROOM_ISLAND);
        this.addAnimalsSpawn("MUSHROOM_SHORE", BiomeBase.MUSHROOM_SHORE);
        this.addAnimalsSpawn("BEACH", BiomeBase.BEACH);
        this.addAnimalsSpawn("DESERT_HILLS", BiomeBase.DESERT_HILLS);
        this.addAnimalsSpawn("FOREST_HILLS", BiomeBase.FOREST_HILLS);
        this.addAnimalsSpawn("TAIGA_HILLS", BiomeBase.TAIGA_HILLS);
        this.addAnimalsSpawn("SMALL_MOUNTAINS", BiomeBase.SMALL_MOUNTAINS);
        this.addAnimalsSpawn("JUNGLE", BiomeBase.JUNGLE);
        this.addAnimalsSpawn("JUNGLE_HILLS", BiomeBase.JUNGLE_HILLS);
        this.addAnimalsSpawn("JUNGLE_EDGE", BiomeBase.JUNGLE_EDGE);
        this.addAnimalsSpawn("STONE_BEACH", BiomeBase.STONE_BEACH);
        this.addAnimalsSpawn("COLD_BEACH", BiomeBase.COLD_BEACH);
        this.addAnimalsSpawn("BIRCH_FOREST", BiomeBase.BIRCH_FOREST);
        this.addAnimalsSpawn("BIRCH_FOREST_HILLS", BiomeBase.BIRCH_FOREST_HILLS);
        this.addAnimalsSpawn("ROOFED_FOREST", BiomeBase.ROOFED_FOREST);
        this.addAnimalsSpawn("COLD_TAIGA", BiomeBase.COLD_TAIGA);
        this.addAnimalsSpawn("COLD_TAIGA_HILLS", BiomeBase.COLD_TAIGA_HILLS);
        this.addAnimalsSpawn("MEGA_TAIGA", BiomeBase.MEGA_TAIGA);
        this.addAnimalsSpawn("MEGA_TAIGA_HILLS", BiomeBase.MEGA_TAIGA_HILLS);
        this.addAnimalsSpawn("EXTREME_HILLS_PLUS", BiomeBase.EXTREME_HILLS_PLUS);
        this.addAnimalsSpawn("SAVANNA", BiomeBase.SAVANNA);
        this.addAnimalsSpawn("SAVANNA_PLATEAU", BiomeBase.SAVANNA_PLATEAU);
        this.addAnimalsSpawn("MESA", BiomeBase.MESA);
        this.addAnimalsSpawn("MESA_PLATEAU_F", BiomeBase.MESA_PLATEAU_F);
        this.addAnimalsSpawn("MESA_PLATEAU", BiomeBase.MESA_PLATEAU);
        this.addAnimalsSpawn("FOREST", BiomeBase.FOREST);

        SpigotConfig.config.set("world-settings.mob-spawn-range", 41);
        SpigotConfig.config.set("world-settings.default.mob-spawn-range", 41);
        SpigotConfig.config.set("spawn-limits.animals", 20);
        SpigotConfig.config.save(new File("spigot.yml"));
    }

    private void addAnimalsSpawn(String name, BiomeBase biomeBase) throws ReflectiveOperationException {
        Field biome = BiomeBase.class.getDeclaredField(name);
        Field defaultMobField = BiomeBase.class.getDeclaredField("au");
        defaultMobField.setAccessible(true);

        List<BiomeBase.BiomeMeta> mobs = new ArrayList<>();

        mobs.add(new BiomeBase.BiomeMeta(EntitySheep.class, 1, 3, 4));
        mobs.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 2, 2, 2));
        mobs.add(new BiomeBase.BiomeMeta(EntityPig.class, 4, 4, 5));
        mobs.add(new BiomeBase.BiomeMeta(EntityChicken.class, 9, 4, 7));
        mobs.add(new BiomeBase.BiomeMeta(EntityCow.class, 9, 4, 7));
        mobs.add(new BiomeBase.BiomeMeta(EntityWolf.class, 4, 1, 4));
        mobs.add(new BiomeBase.BiomeMeta(EntityHorse.class, 1, 2, 5));

        defaultMobField.set(biomeBase, mobs);
        Reflection.setFinalStatic(biome, biomeBase);
    }
}
