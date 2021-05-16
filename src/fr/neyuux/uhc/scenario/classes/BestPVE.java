package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BestPVE extends Scenario implements Listener {
    public BestPVE() {
        super(Scenarios.BEST_PVE, new ItemStack(Material.BONE));
    }

    public static int timer = 15 * 60;

    private static final List<PlayerUHC> bestpve = new ArrayList<>();
    public static final int[] IGtimers = {0};

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        bestpve.addAll(UHC.getInstance().getAlivePlayers());
        IGtimers[0] = timer;
        new BukkitRunnable() {
            @Override
            public void run() {
                IGtimers[0]--;
                if (IGtimers[0] == 0) {
                    for (PlayerUHC pu : bestpve) {
                        pu.maxHealth += 2.0;
                        if (pu.getPlayer().isOnline()) {
                            pu.getPlayer().getPlayer().sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§7Vous avez reçu un coeur grâce à vos compétences en PvE.");
                            pu.getPlayer().getPlayer().setMaxHealth(pu.maxHealth);
                        }
                    }
                    IGtimers[0] = BestPVE.timer;
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


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent ev) {
        Entity e = ev.getEntity();
        if (ev.getEntityType().equals(EntityType.PLAYER) && bestpve.contains(UHC.getInstance().getPlayerUHC((Player)e)) && !ev.isCancelled()) {
            PlayerUHC pu = UHC.getInstance().getPlayerUHC((Player)e);
            bestpve.remove(pu);
            Bukkit.broadcastMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l " + Symbols.DOUBLE_ARROW + pu.getPlayer().getPlayer().getDisplayName() + " §7a pris un dégât et est donc supprimé de la liste BestPvE !");
        }
    }

    @EventHandler
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null && ev.getKiller().isAlive() && !bestpve.contains(ev.getKiller())) {
            bestpve.add(ev.getKiller());
            Bukkit.broadcastMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l " + Symbols.DOUBLE_ARROW + ev.getKiller().getPlayer().getPlayer().getDisplayName() + " §7a fait un kill et retourne donc dans la liste BestPvE !");
        }
    }
}
