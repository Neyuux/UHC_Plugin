package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SkyHigh extends Scenario implements Listener {
    public SkyHigh() {
        super(Scenarios.SKY_HIGH, new ItemStack(Material.LADDER));
    }

    public static int timer = 3600, highMin = 150, enderpearlGives = 0;
    public static double damage = 0.5;
    public static boolean hasStuffDropOnKiller = false;

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);

        final int[] timer = {SkyHigh.timer, 30, 120};
        new BukkitRunnable() {
            @Override
            public void run() {
                timer[0]--;
                if (timer[0] == 0) {
                    Bukkit.broadcastMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cActivation du Scénario ! §cTous les joueurs qui ne sont pas au dessus de la couche "+highMin+" recevront §4§l" + damage + Symbols.HEARTH + " §6de dégâts toutes les 30 secondes à compter de maintenant.");
                    timer[0] = SkyHigh.timer;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            timer[1]--;
                            if (timer[1] == 0) {
                                for (PlayerUHC pu : Index.getInstance().getAlivePlayers())
                                    if (pu.getPlayer().isOnline() && pu.getPlayer().getPlayer().getLocation().getY() < highMin) {
                                        Player p = pu.getPlayer().getPlayer();
                                        p.damage(0);
                                        if (p.getHealth() > damage * 2.0) p.setHealth(p.getHealth() - damage * 2.0);
                                        else new FightListener(Index.getInstance()).eliminate(p, true, null, p.getDisplayName() + " §dest resté trop longtemps en dessous de la couche "+highMin+".");
                                        Index.sendActionBar(p, Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§4Vous perdez " + damage + Symbols.HEARTH + "  en étant en dessous de la couche "+highMin+".");
                                    }
                                timer[1] = 30;
                            }
                            if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
                        }
                    }.runTaskTimer(Index.getInstance(), 0, 20);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            timer[2]--;
                            if (timer[2] == 0 && enderpearlGives != 0) {
                                for (PlayerUHC pu : Index.getInstance().getAlivePlayers())
                                    if (pu.getPlayer().isOnline())
                                        InventoryManager.give(pu.getPlayer().getPlayer(), null, new ItemStack(Material.ENDER_PEARL));
                                    enderpearlGives--;
                                timer[2] = 120;
                            }
                            if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
                        }
                    }.runTaskTimer(Index.getInstance(), 0, 20);
                    cancel();
                }
                if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(Index.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return true;
    }
    
    
    @EventHandler
    public void onElimination(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null)
            if (ev.getKiller().getPlayer().isOnline())
                ev.setStuffLocation(ev.getKiller().getPlayer().getPlayer().getLocation());
            else ev.setStuffLocation(ev.getKiller().getLastLocation());
    }
}
