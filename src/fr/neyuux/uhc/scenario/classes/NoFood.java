package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class NoFood extends Scenario implements Listener {

    public NoFood() {
        super(Scenarios.NO_FOOD, new ItemStack(Material.BREAD));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
        
        for (PlayerUHC pu : UHC.getInstance().getAlivePlayers()) {
            pu.foodLevel = 20;
            if (pu.getPlayer().isOnline()) pu.getPlayer().getPlayer().setFoodLevel(20);
        }
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onFood(FoodLevelChangeEvent ev) {
        ev.setFoodLevel(20);
    }
}
