package fr.neyuux.uhc;

import fr.neyuux.uhc.listeners.WorldListener;
import fr.neyuux.uhc.util.SugarCaneGenerator;
import fr.neyuux.uhc.util.UHCChunkLoader;
import fr.neyuux.uhc.util.VeinGenerator;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class UHCWorld {

    private static World world;
    private static boolean loaded = false;
    private static YamlConfiguration yconfig;
    private static final List<Location> spawns = new ArrayList<>();
    public static final List<Chunk> CORRECTED_CHUNKS = new ArrayList<>();

    public static String MAIN_WORLD = "Core";

    private final UHC main;

    public UHCWorld(UHC main) {
        this.main = main;
        File file = new File(main.getDataFolder(), "config.yml");
        yconfig = YamlConfiguration.loadConfiguration(file);
    }


    public UHCWorld create() {
        Long seed = 0L;
        if (main.mode.equals(UHC.Modes.UHC))
            seed = yconfig.getLongList("uhc_maps").get(new Random().nextInt(yconfig.getLongList("uhc_maps").size()));
        else if (main.mode.equals(UHC.Modes.LG))
            seed = yconfig.getLongList("lg_maps").get(new Random().nextInt(yconfig.getLongList("lg_maps").size()));
        Bukkit.broadcastMessage(UHC.getPrefix() + "§2Création du monde §a\"§l" + seed + "§a\"§2...");

        System.out.println(world);
        world = Bukkit.createWorld(new WorldCreator(seed.toString()).seed(seed));
        MAIN_WORLD = world.getName();
        this.createBarrierPlatform();
        System.out.println(world);
        spawns.clear();
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
        MAIN_WORLD = "Core";
        for (Player p : world.getPlayers())
            p.teleport(this.getPlatformLoc());
        long seed = world.getSeed();
        world.setAutoSave(false);
        //loaded = false;
        Bukkit.unloadWorld(Long.toString(seed), false);
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException ignored) { }
        world = null;
    }

    public void createBarrierPlatform() {
        World w = Bukkit.getWorld(MAIN_WORLD);

        for (int x = -25; x <= 25; x++)
            for (int z = -25; z <= 25; z++) {
                w.getBlockAt(x, 130, z).setType(Material.BARRIER);
                if (x == 25 || x == -25 || z == 25 || z == -25)
                    w.getBlockAt(x, 132, z).setType(Material.BARRIER);
            }
        if (!MAIN_WORLD.equals("Core")) loadLocationChunks(new Location(world, 0, 70, 0), true, false);
    }

    public Location getPlatformLoc() {
        return new Location(Bukkit.getWorld(MAIN_WORLD), new Random().nextInt(50)-24, 131.5, new Random().nextInt(50)-24);
    }

    public void loadChunks() {
        if (loaded) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            double d = 0;
            for (Location l : spawns) {
                loadLocationChunks(l, true, false);
                d++;
                Bukkit.broadcastMessage(UHC.getPrefix() + "§2Chargement du monde : §a§l" + df.format(d / (main.getAlivePlayers().size() + 1) * 100) + "%§2.");
            }
        }
        loadLocationChunks(new Location(world, 0, 70, 0), true, true);
        Bukkit.broadcastMessage(UHC.getPrefix() + "§2Chargement du monde : §a§l100%§2.");
        loaded = true;
    }

    private static List<Chunk> loadLocationChunks(Location loc, Boolean load, boolean correctSpawns) {
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

    public void generateChunks(final World.Environment env) {
        Double size = (Double) GameConfig.ConfigurableParams.BORDERSIZE.getValue();

        if (env == World.Environment.NETHER)
            size /= 2;

        VeinGenerator veinGenerator = new VeinGenerator();
        SugarCaneGenerator sugarCaneGenerator = new SugarCaneGenerator();
        long time = System.currentTimeMillis();
        UHCChunkLoader chunkLoaderThread = new UHCChunkLoader(world, size.intValue(), 250, 18) {
            public void onDoneLoadingWorld() {
                String finalTime = UHC.getTimer(Math.toIntExact((System.currentTimeMillis() - time) / 1000));

                Bukkit.getLogger().info("UHC >> Environment " + env.toString() + " 100% loaded");
                Bukkit.broadcastMessage(UHC.getPrefix() + "§2Préchargement du monde terminé ! §o(Temps utilisé : " + finalTime + ")");
                if (env.equals(World.Environment.NORMAL) && (boolean)GameConfig.ConfigurableParams.NETHER.getValue()) {
                    UHCWorld.this.generateChunks(World.Environment.NETHER);
                }
            }

            public void onDoneLoadingChunk(Chunk chunk) {
                if ((boolean)GameConfig.ConfigurableParams.CORRECT_SPAWNS.getValue() && env.equals(World.Environment.NORMAL)) {
                    veinGenerator.generateInChunk(chunk);
                    sugarCaneGenerator.generateInChunk(chunk);
                }
            }
        };

        chunkLoaderThread.printSettings();

        Bukkit.getScheduler().runTask(UHC.getInstance(), chunkLoaderThread);
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

        WorldListener.keepChunk.addAll(loadLocationChunks(tp, false, false));
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
            final String baseDir = Paths.get("").toAbsolutePath() + File.separator;
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
        border.setSize(fsize, (long) (((size - fsize) / speed)) / 2);
    }

    public static ArmorStand getArmorStand(Location loc) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(false);

        return stand;
    }
}