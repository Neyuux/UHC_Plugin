package fr.neyuux.uhc.scenario.classes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.TeamPrefix;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.util.PlayerSkin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Anonymous extends Scenario implements Listener {

    private final Index main = Index.getInstance();
    private final HashMap<PlayerUHC, String> realName = new HashMap<>();
    public Anonymous() {
        super(Scenarios.ANONYMOUS, new ItemsStack(new ItemStack(Material.SKULL_ITEM, 1, (short)3)).toItemStackwithMinecraftHeadsValueMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThhYWQxNDE4MGU0MWU2OTdkYTVmMjhhOWM4YWEyNmFkOTQ0MjU5OTEwZmNhMWRjODg0ZDJiNWIyMGNiMWVmOCJ9fX0=="));
    }

    public static String usedName = "";

    @Override
    protected void activate() {
        Bukkit.broadcastMessage(main.getPrefix() + "§6Vous pouvez changer le pseudo / skin qui sera utilisé par le Scénario Anonymous avec la commande §b§l/uhc am §a<nom du pseudo/skin>§6. §o(Si vous n'utilisez pas cette commande, le skin et le pseudo appartiendront à un joueur aléatoire de la partie.)");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());

        List<PlayerUHC> choosable = new ArrayList<>();
        for (PlayerUHC pu : main.players) if (!pu.isSpec() && pu.getPlayer().isOnline())
            choosable.add(pu);
        if (usedName.equals("")) usedName = choosable.get(new Random().nextInt(choosable.size())).getPlayer().getName();
        Bukkit.broadcastMessage(main.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §6Identité sélectionnée pour la partie : §b§l" + usedName + "§6.");

        for (PlayerUHC pl : choosable) changeNameAndSkin(pl.getPlayer().getPlayer(), "§k" + usedName, usedName);
    }


    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(main.getPlayerUHC(p).isSpec())
            return;

        changeNameAndSkin(p, "§k" + usedName, usedName);
    }



    public void changeNameAndSkin(Player p, String customName, String skinName) {
        this.realName.put(main.getPlayerUHC(p), p.getName());
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
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(main.getPrefix() + "§cErreur lors du changement du skin de " + p.getName());
        }
    }
}
