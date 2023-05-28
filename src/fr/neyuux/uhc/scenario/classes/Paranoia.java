package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

public class Paranoia extends Scenario implements Listener {
    public Paranoia() {
        super(Scenarios.PARANOIA, new ItemStack(Material.PAPER));
    }

    public static boolean hasMineDiamond = true, hasMineGold = true, hasCraftEnchant = true, hasCraftAnvil = true, hasCraftGoldenApple = true,
            hasCraftGoldenHead = true, hasDeath = true, hasPortalTravel = true, hasUseGoldenApple = true, hasUseGoldenHead = true;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParanoBreakBlock(BlockBreakEvent ev) {
        Player p = ev.getPlayer();
        Location l = p.getLocation();
        Block b = ev.getBlock();
        if (!ev.isCancelled() && UHC.getInstance().getPlayerUHC(p.getUniqueId()).isAlive()) {
            if (hasMineDiamond && b.getType().equals(Material.DIAMOND_ORE))
                Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ba miné du diamant en : §b§lX§b:§3 " + l.getBlockX() + " §b; §lY§b:§3 " + l.getBlockY() + " §b; §lZ§b:§3 " + l.getBlockZ() + " §b; §lMonde§b:§3 " + l.getWorld().getEnvironment().name());
            if (hasMineGold && b.getType().equals(Material.GOLD_ORE))
                Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ea miné de l'or en : §e§lX§e:§6 " + l.getBlockX() + " §e; §lY§e:§6 " + l.getBlockY() + " §e; §lZ§e:§6 " + l.getBlockZ() + " §e; §lMonde§e:§6 " + l.getWorld().getEnvironment().name());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParanoCraft(CraftItemEvent ev) {
        if (ev.getWhoClicked() instanceof Player) {
            Player p = (Player)ev.getWhoClicked();
            Location l = p.getLocation();
            Material m = ev.getCurrentItem().getType();
            if (!ev.isCancelled() && UHC.getInstance().getPlayerUHC(p.getUniqueId()).isAlive()) {
                if (hasCraftEnchant && m.equals(Material.ENCHANTMENT_TABLE))
                    Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §da craft une table d'enchantement en : §d§lX§d:§5 " + l.getBlockX() + " §d; §lY§d:§5 " + l.getBlockY() + " §d; §lZ§d:§5 " + l.getBlockZ() + " §d; §lMonde§d:§5 " + l.getWorld().getEnvironment().name());
                if (hasCraftAnvil && m.equals(Material.ANVIL))
                    Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §7a craft une enclume en : §7§lX§7:§f " + l.getBlockX() + " §7; §lY§7:§f " + l.getBlockY() + " §7; §lZ§7:§f " + l.getBlockZ() + " §7; §lMonde§7:§f " + l.getWorld().getEnvironment().name());
                if (hasCraftGoldenApple && m.equals(Material.GOLDEN_APPLE) && !ev.getCurrentItem().getItemMeta().hasLore())
                    Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ea craft une pomme en or en : §e§lX§e:§6 " + l.getBlockX() + " §e; §lY§e:§6 " + l.getBlockY() + " §e; §lZ§e:§6 " + l.getBlockZ() + " §e; §lMonde§e:§6 " + l.getWorld().getEnvironment().name());
                if (hasCraftGoldenHead && m.equals(Material.GOLDEN_APPLE) && ev.getCurrentItem().getItemMeta().hasLore())
                    Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ea craft une golden head en : §e§lX§e:§6 " + l.getBlockX() + " §e; §lY§e:§6 " + l.getBlockY() + " §e; §lZ§e:§6 " + l.getBlockZ() + " §e; §lMonde§e:§6 " + l.getWorld().getEnvironment().name());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParanoDeath(PlayerEliminationEvent ev) {
        Location l = ev.getPlayerUHC().getLastLocation();
        if (hasDeath) Bukkit.broadcastMessage(getPrefix() + "§4" + ev.getPlayerUHC().getPlayer().getName() + " §cest mort en : §c§lX§c:§4 " + l.getBlockX() + " §c; §lY§c:§4 " + l.getBlockY() + " §c; §lZ§c:§4 " + l.getBlockZ() + " §c; §lMonde§c:§4 " + l.getWorld().getEnvironment().name());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParanoPortalTravel(PlayerPortalEvent ev) {
        Player p = ev.getPlayer();
        Location l = ev.getFrom();
        if (!ev.isCancelled() && hasPortalTravel && UHC.getInstance().getPlayerUHC(p.getUniqueId()).isAlive())
            Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §aa traversé un portail en : §a§lX§a:§2 " + l.getBlockX() + " §a; §lY§a:§2 " + l.getBlockY() + " §a; §lZ§a:§2 " + l.getBlockZ() + " §a; §lMonde§a:§2 " + l.getWorld().getEnvironment().name());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParanoEat(PlayerItemConsumeEvent ev) {
        Player p = ev.getPlayer();
        Location l = p.getLocation();
        ItemStack it = ev.getItem();
        if (!ev.isCancelled() && UHC.getInstance().getPlayerUHC(p.getUniqueId()).isAlive()) {
            if (hasUseGoldenApple && it.getType().equals(Material.GOLDEN_APPLE) && !it.getItemMeta().hasLore())
                Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ea mangé une pomme en or en : §e§lX§e:§6 " + l.getBlockX() + " §e; §lY§e:§6 " + l.getBlockY() + " §e; §lZ§e:§6 " + l.getBlockZ() + " §e; §lMonde§e:§6 " + l.getWorld().getEnvironment().name());
            if (hasUseGoldenHead && it.getType().equals(Material.GOLDEN_APPLE) && it.getItemMeta().hasLore())
                Bukkit.broadcastMessage(getPrefix() + p.getDisplayName() + " §ea mangé une golden head en : §e§lX§e:§6 " + l.getBlockX() + " §e; §lY§e:§6 " + l.getBlockY() + " §e; §lZ§e:§6 " + l.getBlockZ() + " §e; §lMonde§e:§6 " + l.getWorld().getEnvironment().name());
        }
    }
}
