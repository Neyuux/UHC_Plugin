package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FireLess extends Scenario implements Listener {
    public FireLess() {
        super(Scenarios.FIRE_LESS, new ItemStack(Material.FLINT_AND_STEEL));
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


    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER) && (ev.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || ev.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || ev.getCause().equals(EntityDamageEvent.DamageCause.LAVA)))
            ev.setCancelled(true);
    }
}
