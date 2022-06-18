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
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    @Override
    protected void activate() {
        UHC.sendHostMessage(UHC.getPrefix() + "§6Vous pouvez changer le pseudo / skin qui sera utilisé par le Scénario Anonymous avec la commande §b§l/uhc am §a<nom du pseudo/skin>§6. §o(Si vous n'utilisez pas cette commande, le skin et le pseudo appartiendront à un joueur aléatoire de la partie.)");
        if ((boolean) GameConfig.ConfigurableParams.HEAD.getValue() || (boolean) GameConfig.ConfigurableParams.BARRIER_HEAD.getValue())
            Bukkit.broadcastMessage(UHC.getPrefix() + "§cVeuillez désactiver le drop de la tête et la barrière avec la tête pour que " + scenario.getDisplayName() + " §cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        List<PlayerUHC> choosable = new ArrayList<>();
        for (PlayerUHC pu : main.players) if (!pu.isSpec() && pu.getPlayer().isOnline())
            choosable.add(pu);
        if (usedName.equals("")) usedName = choosable.get(new Random().nextInt(choosable.size())).getPlayer().getName();
        Bukkit.broadcastMessage(getPrefix() + "§6Identité sélectionnée pour la partie : §b§l" + usedName + "§6.");

        for (PlayerUHC pl : choosable) changeNameAndSkin(pl.getPlayer().getPlayer(), "§kAnonymous" + used + "§r", usedName);
    }


    @Override
    public boolean checkStart() {
        return !(boolean)GameConfig.ConfigurableParams.HEAD.getValue() && !(boolean)GameConfig.ConfigurableParams.BARRIER_HEAD.getValue();
    }


    @EventHandler
    public void onRel(PluginReloadEvent ev) {
        usedName = "";
        realName.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(!main.getPlayerUHC(p).isSpec()) changeNameAndSkin(p, "§kAnonymous" + used + "§r", usedName);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

        ev.setMessage(message);
    }

    @EventHandler
    public void onEnd(GameEndEvent ev) {
        if (!ev.isCancelled())
            resetNamesAndSkins();
    }



    public static void changeNameAndSkin(Player p, String customName, String skinName) {
        realName.put(main.getPlayerUHC(p), p.getName());

        UHCTeam t = null;
        if (main.getPlayerUHC(p).getTeam() != null) {
            t = main.getPlayerUHC(p).getTeam();
            main.getPlayerUHC(p).getTeam().leave(main.getPlayerUHC(p));

        } else {
            if (main.getPlayerUHC(p).isHost())
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
            PlayerSkin skin = new PlayerSkin(Bukkit.getOfflinePlayer(skinName).getUniqueId().toString().replace("-", ""));
            if (skin.getSkinName() != null)
                gp.getProperties().put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignature()));
            bH.set(entityPlayer, gp);
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(p);
                pl.showPlayer(p);
            }
            main.getPlayerUHC(p).setPlayer(p);
            if (t != null) t.add(p);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(UHC.getPrefix() + "§cErreur lors du changement du skin de " + p.getName());
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
                    PlayerSkin skin = new PlayerSkin(Bukkit.getOfflinePlayer(en.getValue()).getUniqueId().toString().replace("-", ""));
                    if (skin.getSkinName() != null)
                        gp.getProperties().put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignature()));
                    bH.set(entityPlayer, gp);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.hidePlayer(p);
                        pl.showPlayer(p);
                    }
                    en.getKey().setPlayer(p);
                    if (t != null) t.add(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(UHC.getPrefix() + "§cErreur lors du changement du skin de " + p.getName());
                }
            }
    }
}
