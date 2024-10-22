package fr.neyuux.uhc.scenario.classes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.events.GameEndEvent;
import fr.neyuux.uhc.events.PluginReloadEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.teams.UHCTeam;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.util.PlayerSkin;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Anonymous extends Scenario implements Listener {

    public Anonymous() {
        super(Scenarios.ANONYMOUS, new ItemsStack(new ItemStack(Material.SKULL_ITEM, 1, (short)3)).toItemStackwithMinecraftHeadsValueMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThhYWQxNDE4MGU0MWU2OTdkYTVmMjhhOWM4YWEyNmFkOTQ0MjU5OTEwZmNhMWRjODg0ZDJiNWIyMGNiMWVmOCJ9fX0=="));
    }

    public static String usedName = "";
    private static int used;
    private static final HashMap<PlayerUHC, String> realName = new HashMap<>();
    private static final UHC main = UHC.getInstance();
    private PlayerSkin skin;
    private static final HashMap<PlayerUHC, PlayerSkin> realSkin = new HashMap<>();

    @Override
    protected void activate() {
        UHC.sendHostMessage(UHC.getPrefix() + "�6Vous pouvez changer le pseudo / skin qui sera utilis� par le Sc�nario Anonymous avec la commande �b�l/uhc am �a<nom du pseudo/skin>�6. �o(Si vous n'utilisez pas cette commande, le skin et le pseudo appartiendront � un joueur al�atoire de la partie.)");
        if ((boolean) GameConfig.ConfigurableParams.HEAD.getValue() || (boolean) GameConfig.ConfigurableParams.BARRIER_HEAD.getValue())
            Bukkit.broadcastMessage(UHC.getPrefix() + "�cVeuillez d�sactiver le drop de la t�te et la barri�re avec la t�te pour que " + scenario.getDisplayName() + " �cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        List<PlayerUHC> choosable = new ArrayList<>();
        for (PlayerUHC pu : main.players) if (!pu.isSpec() && pu.getPlayer().isOnline())
            choosable.add(pu);
        if (usedName.equals("")) usedName = choosable.get(new Random().nextInt(choosable.size())).getPlayer().getName();
        Bukkit.broadcastMessage(getPrefix() + "�6Identit� s�lectionn�e pour la partie : �b�l" + usedName + "�6.");

        this.skin = new PlayerSkin(Bukkit.getOfflinePlayer(usedName).getUniqueId());

        for (PlayerUHC pl : choosable) changeNameAndSkin(pl.getPlayer().getPlayer(), "�kAnonymous" + used + "�r", skin);
    }


    @Override
    public boolean checkStart() {
        return !(boolean)GameConfig.ConfigurableParams.HEAD.getValue() && !(boolean)GameConfig.ConfigurableParams.BARRIER_HEAD.getValue();
    }


    @EventHandler
    public void onRel(PluginReloadEvent ev) {
        usedName = "";
        this.skin = null;
        realName.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(!main.getPlayerUHC(p.getUniqueId()).isSpec()) changeNameAndSkin(p, "�kAnonymous" + used + "�r", this.skin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent ev) {
        String message = ev.getMessage();
        String[] messageWords = message.split(" ");
        List<String> broadcastAliases = new ArrayList<>(Arrays.asList("/b", "/broadcast", "/bd", "/bc", "/annonce", "/announce", "/ann", "/say"));
        List<String> directMessageAliases = new ArrayList<>(Arrays.asList("/msg", "/m", "/message", "/tell"));

        if (messageWords.length <= 1) return;

        if (directMessageAliases.contains(messageWords[0].toLowerCase())) {
            String receiver = messageWords[1];

            for (Map.Entry<PlayerUHC, String> name : realName.entrySet())
                if (receiver.equalsIgnoreCase(name.getValue()))
                    message = message.replace(receiver, name.getKey().getPlayer().getName());

        } else if (!broadcastAliases.contains(messageWords[0].toLowerCase()) && !message.toLowerCase().startsWith("/uhc t")) {

            for (Map.Entry<PlayerUHC, String> name : realName.entrySet())
                message = message.replaceAll("(?i)" + name.getValue(), name.getKey().getPlayer().getName());
        }

        if (!Objects.equals(ev.getMessage(), message))
            Bukkit.getLogger().info("UHC >> Anonymous >> changed name in command : " + message);
        ev.setMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnd(GameEndEvent ev) {
        if (!ev.isCancelled())
            resetNamesAndSkins();
    }



    public static void changeNameAndSkin(Player p, String customName, PlayerSkin skin) {
        realName.put(main.getPlayerUHC(p.getUniqueId()), p.getName());
        realSkin.put(main.getPlayerUHC(p.getUniqueId()), new PlayerSkin(p.getUniqueId()));

        UHCTeam t = null;
        if (main.getPlayerUHC(p.getUniqueId()).getTeam() != null) {
            t = main.getPlayerUHC(p.getUniqueId()).getTeam();
            main.getPlayerUHC(p.getUniqueId()).getTeam().leave(main.getPlayerUHC(p.getUniqueId()));

        } else {
            if (main.getPlayerUHC(p.getUniqueId()).isHost())
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").removeEntry(p.getName());
        }

        p.setDisplayName(p.getDisplayName().replace(p.getName(), customName).replace(TeamPrefix.getHostPrefix(), ""));
        p.setPlayerListName(p.getDisplayName());

        try {
            Method getHandle = p.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(p);
            Class<?> entityHuman = entityPlayer.getClass().getSuperclass();
            Field bH = entityHuman.getDeclaredField("bH");
            bH.setAccessible(true);
            GameProfile gp = new GameProfile(p.getUniqueId(), customName);
            if (skin.getSkinName() != null)
                gp.getProperties().put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignature()));
            bH.set(entityPlayer, gp);
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(p);
                pl.showPlayer(p);
            }
            main.getPlayerUHC(p.getUniqueId()).setPlayer(p);
            if (t != null) t.add(p);
            reloadSkinForSelf(p);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(UHC.getPrefix() + "�cErreur lors du changement du skin de " + p.getName());
        }
        used++;
        if (main.isState(Gstate.PLAYING))
            new BukkitRunnable() {
                @Override
                public void run() {
                    main.setGameScoreboard(p);
                }
            }.runTaskLater(main, 60);
    }
    
    public void resetNamesAndSkins() {
        for (Map.Entry<PlayerUHC, String> en : realName.entrySet())
            if (en.getKey().getPlayer().isOnline()) {
                Player p = en.getKey().getPlayer().getPlayer();
                UHCTeam t = null;
                if (en.getKey().getTeam() != null) {
                    t = en.getKey().getTeam();
                    en.getKey().getTeam().leave(en.getKey());
                }
                try {
                    Method getHandle = p.getClass().getMethod("getHandle");
                    Object entityPlayer = getHandle.invoke(p);
                    Class<?> entityHuman = entityPlayer.getClass().getSuperclass();
                    Field bH = entityHuman.getDeclaredField("bH");
                    bH.setAccessible(true);
                    GameProfile gp = new GameProfile(p.getUniqueId(), en.getValue());
                    PlayerSkin skin = realSkin.get(en.getKey());
                    if (skin.getSkinName() != null)
                        gp.getProperties().put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignature()));
                    bH.set(entityPlayer, gp);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.hidePlayer(p);
                        pl.showPlayer(p);
                    }
                    en.getKey().setPlayer(p);
                    if (t != null) t.add(p);
                    reloadSkinForSelf(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(UHC.getPrefix() + "�cErreur lors du changement du skin de " + p.getName());
                }
            }
    }

    public static void reloadSkinForSelf(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        Location loc = player.getLocation().clone();

        ep.playerConnection.sendPacket(removeInfo);
        ep.playerConnection.sendPacket(addInfo);

        player.teleport(new Location(Bukkit.getWorld("Core"), 0, 100, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                ep.playerConnection.sendPacket(new PacketPlayOutRespawn(ep.dimension, ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode()));
                player.updateInventory();
            }
        }.runTaskLater(UHC.getInstance(), 2L);
    }
}
