package fr.neyuux.uhc;

import com.google.common.util.concurrent.AtomicDouble;
import fr.neyuux.uhc.listeners.WorldListener;
import fr.neyuux.uhc.util.SugarCaneGenerator;
import fr.neyuux.uhc.util.UHCChunkLoader;
import fr.neyuux.uhc.util.VeinGenerator;
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
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class UHCWorld {

    private static World world;
    private static World nether;
    private static World end;
    private static boolean loaded = false;
    private static YamlConfiguration yconfig;
    private static final List<Location> spawns = new ArrayList<>();
    public static final List<Chunk> CORRECTED_CHUNKS = new ArrayList<>();
    private static final List<World> worlds = new ArrayList<>();
    private static final List<Block> toRemovePlatform = new ArrayList<>();

    private static boolean skydefender = false;

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
        nether = Bukkit.createWorld(new WorldCreator(seed + "_nether").environment(World.Environment.NETHER));
        end = Bukkit.createWorld(new WorldCreator(seed + "_the_end").environment(World.Environment.THE_END));

        worlds.add(world);
        worlds.add(nether);
        worlds.add(end);

        MAIN_WORLD = world.getName();
        this.createBarrierPlatform();
        System.out.println(world);
        spawns.clear();
        return this;
    }

    public static World addWorld(String worldname, boolean main, Consumer<World> consumer) {

        World world = Bukkit.getWorld("skydefender");

        if (world == null)
            Bukkit.createWorld(new WorldCreator(worldname));

        worlds.add(world);
        if (main) MAIN_WORLD = worldname;

        consumer.accept(world);

        return world;
    }

    public static World addWorld(World world, boolean main) {
        worlds.add(world);
        if (main) MAIN_WORLD = world.getName();
        return world;
    }

    public static boolean isCreated() {
        return world != null;
    }

    public long getSeed() {
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) return 0;
        return w.getSeed();
    }

    public void changePVP(boolean value) {
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        w.setPVP(value);
        if (getNether() != null) getNether().setPVP(value);
        if (getEnd() != null) getEnd().setPVP(value);
    }

    public Boolean hasPvP() {
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        return w.getPVP();
    }

    public void setDayCycle(Boolean value) {
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        w.setGameRuleValue("doDaylightCycle", value.toString().toLowerCase());
    }

    public void setTime(long value) {
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        w.setTime(value);
    }

    public void delete() {
        if (world == null || MAIN_WORLD.equals("skydefender")) return;
        MAIN_WORLD = "Core";
        for (Player p : Bukkit.getOnlinePlayers())
            p.teleport(this.getPlatformLoc());
        long seed = world.getSeed();
        world.setAutoSave(false);
        //loaded = false;

        for (World world : worlds) {
            Bukkit.unloadWorld(world, false);
            try {
                FileUtils.deleteDirectory(world.getWorldFolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        world = null;
        nether = null;
        end = null;
    }

    public void createBarrierPlatform() {
        if (MAIN_WORLD.equals("skydefender"))
            return;
        World w = Bukkit.getWorld(MAIN_WORLD);

        for (int x = -25; x <= 25; x++)
            for (int z = -25; z <= 25; z++) {
                Block b = w.getBlockAt(x, 130, z);

                b.setType(Material.BARRIER);
                toRemovePlatform.add(b);

                if (x == 25 || x == -25 || z == 25 || z == -25) {
                    Block b2 = w.getBlockAt(x, 132, z);

                    b2.setType(Material.BARRIER);
                    toRemovePlatform.add(b2);
                }
            }
        if (!MAIN_WORLD.equals("Core")) loadLocationChunks(new Location(w, 0, 70, 0), true, false);
    }

    public Location getPlatformLoc() {
        return new Location(Bukkit.getWorld(MAIN_WORLD), new Random().nextInt(48)-24, 131.5, new Random().nextInt(48)-24);
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
        loadLocationChunks(new Location(Bukkit.getWorld(MAIN_WORLD), 0, 70, 0), true, true);
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

    public void generateChunks(final World world) {
        Double size = (Double) GameConfig.ConfigurableParams.BORDERSIZE.getValue();
        World.Environment env = world.getEnvironment();

        if (env == World.Environment.NETHER)
            size /= 2;

        VeinGenerator veinGenerator = new VeinGenerator();
        SugarCaneGenerator sugarCaneGenerator = new SugarCaneGenerator();
        long time = System.currentTimeMillis();
        UHCChunkLoader chunkLoaderThread = new UHCChunkLoader(world, size.intValue(), 300, 10) {
            public void onDoneLoadingWorld() {
                String finalTime = UHC.getTimer(Math.toIntExact((System.currentTimeMillis() - time) / 1000));

                Bukkit.getLogger().info("UHC >> Environment " + env.toString() + " 100% loaded");
                Bukkit.broadcastMessage(UHC.getPrefix() + "§2Préchargement du monde terminé ! §o(Temps utilisé : " + finalTime + ")");
                if (env.equals(World.Environment.NORMAL) && (boolean)GameConfig.ConfigurableParams.NETHER.getValue()) {
                    UHCWorld.this.generateChunks(getNether());
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
        World w = Bukkit.getWorld(MAIN_WORLD);
        
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        boolean farEnought = true;

        for (Location l : list)
            if (l.distance(loc) < player || loc.distance(new Location(w, 0, 0, 0)) < center
                    || (double) GameConfig.ConfigurableParams.BORDERSIZE.getValue() - loc.getBlockX() < border
                    || (double) GameConfig.ConfigurableParams.BORDERSIZE.getValue() - loc.getBlockZ() < border) {
                farEnought = false;
                break;
            }

        return farEnought;
    }


    public List<Location> getSpawns() { return spawns; }

    public static Location getRandomLocation(World w, int size, int maxTentative) {
        List<Material> mat = Arrays.asList(Material.WATER, Material.LAVA, Material.AIR, Material.WATER_LILY, Material.STATIONARY_WATER, Material.STATIONARY_LAVA);
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
        World w = Bukkit.getWorld(MAIN_WORLD);
        if (w == null) throw new NullPointerException("Aucun monde n est cree");
        Location tp = null;
        int border = (int)Math.round((double) GameConfig.ConfigurableParams.BORDERSIZE.getValue());
        while (tp == null || !isFarEnough(new ArrayList<>(spawns), tp, main.getAlivePlayers().size(), border/10/2, border/10))
            tp = getRandomLocation(w, (int)Math.round((double) GameConfig.ConfigurableParams.BORDERSIZE.getValue()) / 2, 50);

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

    public static World getNether() {
        return nether;
    }

    public static World getEnd() {
        return end;
    }

    public static boolean isSkydefender() {
        return skydefender;
    }

    public static void setSkydefender(boolean skydefender) {
        UHCWorld.skydefender = skydefender;
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

    public static void removePlatform() {
        for (Block block : toRemovePlatform) {
            block.setType(Material.AIR);
        }
    }

    public void initialiseWorlds() {
        this.initWorld(Bukkit.getWorld(MAIN_WORLD));
        if ((boolean)GameConfig.ConfigurableParams.NETHER.getValue())
            this.initWorld(getNether());
        if ((boolean)GameConfig.ConfigurableParams.END.getValue())
            this.initWorld(getEnd());
    }

    private void initWorld(World world) {
        int size = (int) Math.round((double)GameConfig.ConfigurableParams.BORDERSIZE.getValue());
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
        World world = Bukkit.getWorld(MAIN_WORLD);
        WorldBorder border = world.getWorldBorder();
        final double speed = (double)GameConfig.ConfigurableParams.BORDERSPEED.getValue();
        final double fsize = (double) GameConfig.ConfigurableParams.FINAL_BORDERSIZE.getValue();
        final double diff = Math.abs(border.getSize() - fsize);
        final double time = diff / speed;
        border.setSize(fsize, (long) time);
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