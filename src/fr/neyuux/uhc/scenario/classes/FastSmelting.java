package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSmelting extends Scenario implements Listener {
    public FastSmelting() {
        super(Scenarios.FAST_SMELTING, new ItemStack(Material.FURNACE));
    }

    public static int smeltSpeed = 3;


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
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        Furnace block = (Furnace)event.getBlock().getState();
        new BukkitRunnable() {
            public void run() {
                if ((block.getCookTime() > 0) || (block.getBurnTime() > 0)) {
                    block.setCookTime((short)(block.getCookTime() + smeltSpeed));
                    block.update();
                } else cancel();
            }
        }.runTaskTimer(UHC.getInstance(), 1L, 1L);
    }
}
