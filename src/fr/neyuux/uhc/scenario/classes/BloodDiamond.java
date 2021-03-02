package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
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
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }

    @EventHandler
    public void onMineDiamond(BlockBreakEvent ev) {
        PlayerUHC pu = Index.getInstance().getPlayerUHC(ev.getPlayer());
        if (ev.getBlock().getType().equals(Material.DIAMOND_ORE) && pu.isAlive()) {
            ev.getPlayer().damage(0);
            if (ev.getPlayer().getHealth() > damage * 2.0) ev.getPlayer().setHealth(ev.getPlayer().getHealth() - damage * 2.0);
            else new FightListener(Index.getInstance()).eliminate(ev.getPlayer(), true, null, ev.getPlayer().getDisplayName() + " §da miné trop de diamants.");
            Index.sendActionBar(ev.getPlayer(), Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§4Vous perdez " + damage + " coeurs en minant du diamant.");
        }
    }
}
