package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SkyHigh extends Scenario implements Listener {
    public SkyHigh() {
        super(Scenarios.SKY_HIGH, new ItemStack(Material.LADDER));
    }

    public static int timer = 3600, highMin = 150, enderpearlGives = 0;
    public static double damage = 0.5;
    public static boolean hasStuffDropOnKiller = false;
    public static final int[] IGtimers = {0, 30, 120};

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        UHC.getInstance().getAlivePlayers().forEach(playerUHC -> playerUHC.getPlayer().getPlayer().getInventory().addItem(new ItemStack(Material.DIRT, 2)));

        IGtimers[0] = SkyHigh.timer;
        new BukkitRunnable() {
            @Override
            public void run() {
                IGtimers[0]--;
                if (IGtimers[0] == 0) {
                    Bukkit.broadcastMessage(getPrefix() + "§cActivation du Scénario ! §cTous les joueurs qui ne sont pas au dessus de la couche "+highMin+" recevront §4§l" + damage + Symbols.HEARTH + " §6de dégâts toutes les 30 secondes à compter de maintenant.");
                    IGtimers[0] = SkyHigh.timer;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            IGtimers[1]--;
                            if (IGtimers[1] == 0) {
                                for (PlayerUHC pu : UHC.getInstance().getAlivePlayers())
                                    if (pu.getPlayer().isOnline() && pu.getPlayer().getPlayer().getLocation().getY() < highMin) {
                                        Player p = pu.getPlayer().getPlayer();
                                        p.damage(0);
                                        if (p.getHealth() > damage * 2.0) p.setHealth(p.getHealth() - damage * 2.0);
                                        else new FightListener(UHC.getInstance()).eliminate(p, true, null, p.getDisplayName() + " §dest resté trop longtemps en dessous de la couche "+highMin+".");
                                        UHC.sendActionBar(p, getPrefix() + "§4Vous perdez " + damage + Symbols.HEARTH + "  en étant en dessous de la couche "+highMin+".");
                                    }
                                IGtimers[1] = 30;
                            }
                            if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
                        }
                    }.runTaskTimer(UHC.getInstance(), 0, 20);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            IGtimers[2]--;
                            if (IGtimers[2] == 0 && enderpearlGives != 0) {
                                for (PlayerUHC pu : UHC.getInstance().getAlivePlayers())
                                    if (pu.getPlayer().isOnline())
                                        InventoryManager.give(pu.getPlayer().getPlayer(), null, new ItemStack(Material.ENDER_PEARL));
                                    enderpearlGives--;
                                IGtimers[2] = 120;
                            }
                            if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
                        }
                    }.runTaskTimer(UHC.getInstance(), 0, 20);
                    cancel();
                }
                if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return true;
    }
    
    
    @EventHandler
    public void onElimination(PlayerEliminationEvent ev) {
        PlayerUHC killer = ev.getKiller();
        if (killer != null && hasStuffDropOnKiller)
            if (killer.getPlayer().isOnline())
                ev.setStuffLocation(killer.getPlayer().getPlayer().getLocation());
            else ev.setStuffLocation(killer.getLastLocation());
    }

    @EventHandler
    public void onPlaceDirt(BlockPlaceEvent ev) {
        if (ev.getBlock().getType().equals(Material.DIRT)) ev.getItemInHand().setAmount(2);
    }
}
