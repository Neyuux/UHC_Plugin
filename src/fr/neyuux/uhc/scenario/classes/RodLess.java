package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class RodLess extends Scenario implements Listener {
    public RodLess() {
        super(Scenarios.ROD_LESS, new ItemStack(Material.FISHING_ROD));
    }

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
    public void onDamageByRod(EntityDamageByEntityEvent ev) {
        if(!(ev.getDamager() instanceof FishHook)) return;
        if(!(ev.getEntity() instanceof Player)) return;
        FishHook a = (FishHook) ev.getDamager();
        if(!(a.getShooter() instanceof Player)) return;
        ((Player)a.getShooter()).sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLa canne à pêche est désactivée en PvP !");
        ev.setCancelled(true);
    }
}
