package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BareBones extends Scenario implements Listener {
    public BareBones() {
        super(Scenarios.BAREBONES, new ItemStack(Material.STRING));
    }

    public static int diamonds = 1;
    public static int gApples = 1;
    public static int arrows = 32;
    public static int strings = 2;

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null) {
            ev.getPlayerUHC().getLastLocation().getWorld().dropItem(ev.getPlayerUHC().getLastLocation(), new ItemStack(Material.DIAMOND, diamonds));
            ev.getPlayerUHC().getLastLocation().getWorld().dropItem(ev.getPlayerUHC().getLastLocation(), new ItemStack(Material.GOLDEN_APPLE, gApples));
            ev.getPlayerUHC().getLastLocation().getWorld().dropItem(ev.getPlayerUHC().getLastLocation(), new ItemStack(Material.ARROW, arrows));
            ev.getPlayerUHC().getLastLocation().getWorld().dropItem(ev.getPlayerUHC().getLastLocation(), new ItemStack(Material.STRING, strings));
        }
    }
}
