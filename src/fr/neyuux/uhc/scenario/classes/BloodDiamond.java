package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BloodDiamond extends Scenario implements Listener {
    public BloodDiamond() {
        super(Scenarios.BLOOD_DIAMOND, new ItemStack(Material.DIAMOND_ORE));
    }

    public static double damage = 0.5;

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
    public void onMineDiamond(BlockBreakEvent ev) {
        PlayerUHC pu = UHC.getInstance().getPlayerUHC(ev.getPlayer());
        if (ev.getBlock().getType().equals(Material.DIAMOND_ORE) && pu.isAlive()) {
            ev.getPlayer().damage(0);
            if (ev.getPlayer().getHealth() > damage * 2.0) ev.getPlayer().setHealth(ev.getPlayer().getHealth() - damage * 2.0);
            else new FightListener(UHC.getInstance()).eliminate(ev.getPlayer(), true, null, ev.getPlayer().getDisplayName() + " §da miné trop de diamants.");
            UHC.sendActionBar(ev.getPlayer(), getPrefix() + "§4Vous perdez " + damage + " coeurs en minant du diamant.");
        }
    }
}
