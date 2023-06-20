package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class BleedingSweets extends Scenario implements Listener {
    public BleedingSweets() {
        super(Scenarios.BLEEDING_SWEETS, new ItemStack(Material.STRING));
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
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null) {
            ArrayList<ItemStack> l = new ArrayList<>(Arrays.asList(ev.getPlayerUHC().getLastInv()));
            if (diamonds != 0) l.add(new ItemStack(Material.DIAMOND, diamonds));
            if (gApples != 0) l.add(new ItemStack(Material.GOLDEN_APPLE, gApples));
            if (arrows != 0) l.add(new ItemStack(Material.ARROW, arrows));
            if (strings != 0) l.add(new ItemStack(Material.STRING, strings));
            ev.getPlayerUHC().setLastInv(l.toArray(new ItemStack[0]));
        }
    }
}
