package fr.neyuux.uhc;

import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.listeners.WorldListener;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UHCWorld {

    private static World world;
    private static boolean loaded = false;
    private static YamlConfiguration yconfig;
    private static final List<Location> spawns = new ArrayList<>();

    private final Index main;

    public UHCWorld(Index main) {
        this.main = main;
        File file = new File(main.getDataFolder(), "config.yml");
        yconfig = YamlConfiguration.loadConfiguration(file);
    }


    public UHCWorld create() {
        Long seed = (long)0;
        if (main.mode.equals(Index.Modes.UHC))
            seed = yconfig.getLongList("uhc_maps").get(new Random().nextInt(yconfig.getLongList("uhc_maps").size()));
        else if (main.mode.equals(Index.Modes.LG))
            seed = yconfig.getLongList("lg_maps").get(new Random().nextInt(yconfig.getLongList("lg_maps").size()));
        Bukkit.broadcastMessage(main.getPrefix() + "§2Création du monde §a\"§l" + seed + "§a\"§2...");

        world = Bukkit.createWorld(new WorldCreator(seed.toString()).seed(seed));
        return this;
    }

    public static boolean isCreated() {
        return world != null;
    }

    public long getSeed() {
        if (world == null) return 0;
        return world.getSeed();
    }

    public void changePVP(boolean value) {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        world.setPVP(value);
    }

    public Boolean hasPvP() {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        return world.getPVP();
    }

    public void setDayCycle(Boolean value) {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        world.setGameRuleValue("doDaylightCycle", value.toString().toLowerCase());
    }

    public void setTime(long value) {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        world.setTime(value);
    }

    public void delete() {
        if (world == null) return;
        long seed = world.getSeed();
        world.setAutoSave(false);
        try {
            MinecraftServer.getServer().worlds.removeIf(ws -> ws.getSeed() == seed);
        } catch (Exception ignored) {}
        for (Chunk c : world.getLoadedChunks())
            c.unload(false);
        File session = new File(Bukkit.getWorldContainer(), seed + "/session.lock");
        session.delete();
        try {
            session.createNewFile();

            try (DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(session))) {
                dataoutputstream.writeLong(System.currentTimeMillis());
            }
        } catch (IOException ignored) {
        }
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException ignored) { }
        world = null;
        loaded = false;
        Bukkit.unloadWorld(Long.toString(seed), false);
    }

    /*public boolean isLoaded() {
        return loaded;
    }

    public void preGenerateWorld() {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        final ArrayList<Chunk> loaded = new ArrayList<>();
        int radius = (int) Math.round((double)GameConfig.ConfigurableParams.BORDERSIZE.getValue());
        String start = new Date().toString();

        int maxX = 17 + radius;
        int maxZ = 17 + radius;
        int minX = 1 - radius;
        int minZ = 1 - radius;
        int fail = 0;
        int total = 0;
        for (int x = minX; x < maxX; x += 16) {
            System.out.println("Generating Chunks for X: " + x + " Z: " + minZ + " to " + maxZ);
            for (int z = minZ; z < maxZ; z += 16) {
                if (!world.loadChunk(x, z, true))
                    fail++;
                total++;
                loaded.add(world.getChunkAt(x, z));
                DecimalFormat f = new DecimalFormat();
                f.setMaximumFractionDigits(2);
                Bukkit.broadcastMessage(main.getPrefix() + "§2Chargement du Monde à §a§l" + f.format((int) Math.round((double) GameConfig.ConfigurableParams.BORDERSIZE.getValue()) / 16 / total * 100) + "%§2.");
                if (loaded.size() == 2000) {
                    System.out.println("Unloading loaded chunks");
                    for (Chunk load : loaded)
                        load.unload();
                    loaded.clear();
                }
            }
            Runtime memCheck = Runtime.getRuntime();
            while (memCheck.freeMemory() / memCheck.maxMemory() < 1) {
                System.out.println("Free memory in heap below threshold, waiting 30 seconds");
                System.out.println("Free (%): " + (memCheck.freeMemory() / memCheck.maxMemory()));
                System.gc();
                try {
                    Thread.sleep(30000L);
                } catch (Exception ignored) {}
                memCheck = Runtime.getRuntime();
            }
        }
        System.out.println("Execution Time:");
        System.out.println("Start: " + start);
        System.out.println("Finish: " + (new Date()).toString());
        System.out.println("Failed Chunks: " + fail + ", Loaded: " + (total - fail) + " Chunks: " + total);
        System.out.println("Finished");
        UHCWorld.loaded = true;
    }*/

    public void loadChunks() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        double d = 0;
        for (Location l : spawns) {
            loadLocationChunks(l, true);
            d++;
            Bukkit.broadcastMessage(main.getPrefix() + "§2Chargement du monde : §a§l" + df.format(d / (main.getAlivePlayers().size() + 1)) + "%§2.");
        }
        loadLocationChunks(new Location(world, 0, 70, 0), true);
        Bukkit.broadcastMessage(main.getPrefix() + "§2Chargement du monde : §a§l100%§2.");
    }

    private static List<Chunk> loadLocationChunks(Location loc, Boolean load) {
        List<Chunk> chunks = new ArrayList<>();

        int cx = loc.getBlockX() - 50;
        int cz = loc.getBlockZ() - 50;

        while (cx <= loc.getBlockX() + 50) {
            while (cz <= loc.getBlockZ() + 50) {
                System.out.println("Chargement chunk a x:" + cx + " z:" + cz);
                Location l = new Location(loc.getWorld(), cx, 0, cz);
                if (load && !l.getChunk().isLoaded())
                    l.getWorld().loadChunk(l.getChunk().getX(), l.getChunk().getZ(), true);
                chunks.add(l.getChunk());
                System.out.println("Chargement reussi");
                DecimalFormat f = new DecimalFormat();
                f.setMaximumFractionDigits(2);
                cz += 16;
            }
            cz = loc.getBlockZ() - 50;
            cx += 16;
        }

        return chunks;
    }

    public static boolean isFarEnough(ArrayList<Location> list, Location loc, Integer player, Integer center, Integer border) {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        boolean farEnought = true;

        for (Location l : list)
            if (l.distance(loc) < player || loc.distance(new Location(world, 0, 0, 0)) < center
                    || (double) GameConfig.ConfigurableParams.BORDERSIZE.getValue() - loc.getBlockX() < border
                    || (double) GameConfig.ConfigurableParams.BORDERSIZE.getValue() - loc.getBlockZ() < border) {
                farEnought = false;
                break;
            }

        return farEnought;
    }


    public List<Location> getSpawns() { return spawns; }

    public static Location getRandomLocation(World w, int size, int maxTentative) {
        List<Material> mat = Arrays.asList(Material.WATER, Material.LAVA, Material.LEAVES, Material.AIR, Material.WATER_LILY, Material.STATIONARY_WATER, Material.STATIONARY_LAVA);
        for (int i = 0; i <= maxTentative; i++) {
            int x = new Random().nextInt(size - (-size) + 1) + (-size);
            int z = new Random().nextInt(size - (-size) + 1) + (-size);

            Location loc = new Location(w, x, 0, z);
            loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

            if (loc != null && loc.getY() <= 160 && !mat.contains(loc.getBlock().getRelative(BlockFace.DOWN).getType()) && isInBorder(loc))
                return loc.add(0, 40, 0);
        }

        return null;
    }

    public void addSpawnLoad() {
        if (world == null) throw new NullPointerException("Aucun monde n est cree");
        Location tp = null;
        int border = (int)Math.round((double) GameConfig.ConfigurableParams.BORDERSIZE.getValue());
        while (tp == null || !isFarEnough(new ArrayList<>(spawns), tp, main.getAlivePlayers().size(), border/10/2, border/10))
            tp = getRandomLocation(world, (int)Math.round((double) GameConfig.ConfigurableParams.BORDERSIZE.getValue()) / 2, 50);

        WorldListener.keepChunk.addAll(loadLocationChunks(tp, false));
        spawns.add(tp);
    }

    public static Boolean isOnSurface(Location loc) {
        return loc.getBlock().getLightFromSky() >= 1;
    }

    public static Boolean isInWater(Location loc) {
        int number = 0;
        while (loc.clone().add(0,1,0).getBlock().getType() == Material.WATER || loc.clone()
                .add(0,1,0).getBlock().getType() == Material.STATIONARY_WATER) {
            number++;
            loc = loc.add(0,1,0);
        }
        return number >= 4;
    }

    public static Boolean isInBorder(Location loc) {
        return loc.getX() <= loc.getWorld().getWorldBorder().getSize() / 2 && loc.getZ() <= loc.getWorld().getWorldBorder().getSize() / 2;
    }

    public static void setAchievements(Boolean value) {
        final CraftServer server = (CraftServer) Bukkit.getServer();
        final MinecraftServer minecraftServer = server.getServer();
        final PropertyManager manager = minecraftServer.getPropertyManager();
        manager.properties.setProperty("announce-player-achievements", value.toString().toLowerCase());
        try {
            final String baseDir = Paths.get("").toAbsolutePath().toString() + File.separator;
            final File f = new File(baseDir + "server.properties");
            final OutputStream out = new FileOutputStream(f);
            manager.properties.store(out, "EditedByUHC");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialiseWorldBorder() {
        int size = (int) Math.round((double)GameConfig.ConfigurableParams.BORDERSIZE.getValue());
        World world = Bukkit.getWorld("" + getSeed());
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(size);
        border.setDamageAmount(0.2);
        border.setDamageBuffer(1);
        border.setWarningTime(10);
        border.setWarningDistance(0);
        world.setDifficulty(Difficulty.HARD);
        world.setTime(0);
        world.setGameRuleValue("naturalRegeneration", "false");
        this.changePVP(false);
    }

    public void startWorldBorder() {
        int size = (int) Math.round((double)GameConfig.ConfigurableParams.BORDERSIZE.getValue());
        int fsize = (int) Math.round((double)GameConfig.ConfigurableParams.FINAL_BORDERSIZE.getValue());
        double speed = Math.round((double)GameConfig.ConfigurableParams.BORDERSPEED.getValue());
        World world = Bukkit.getWorld("" + getSeed());
        WorldBorder border = world.getWorldBorder();
        border.setSize(fsize, (long) ((size - fsize) / speed));
    }

    public static ArmorStand getArmorStand(Location loc) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(false);

        return stand;
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++)
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++)
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++)
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
        return blocks;
    }
}