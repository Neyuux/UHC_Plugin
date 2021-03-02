package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class NoFall extends Scenario implements Listener {
    public NoFall() {
        super(Scenarios.NO_FALL, new ItemStack(Material.GOLD_BOOTS));
    }

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
    public void onFall(EntityDamageEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER) && ev.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            ev.setCancelled(true);
    }
}
