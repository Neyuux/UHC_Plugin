package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NineSlots extends Scenario implements Listener {
    public NineSlots() {
        super(Scenarios.NINE_SLOTS, new ItemStack(Material.STAINED_GLASS, 1, (short)7));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);

        for (PlayerUHC pu : Index.getInstance().getAlivePlayers())
            for (int i = 8; i <= 35; i++) {
                ItemStack it = pu.getPlayer().getPlayer().getInventory().getItem(i);
                if (it != null && !it.getType().equals(Material.AIR)) pu.getPlayer().getPlayer().getWorld().dropItem(pu.getPlayer().getPlayer().getLocation(), it);
                pu.getPlayer().getPlayer().getInventory().setItem(i, new ItemsStack(Material.STAINED_GLASS_PANE, (short)7, scenario.getDisplayName()).toItemStack());
            }
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onMooveGlass(InventoryClickEvent ev) {
        if (ev.getCurrentItem().equals(new ItemsStack(Material.STAINED_GLASS_PANE, (short)7, scenario.getDisplayName()).toItemStack())) ev.setCancelled(true);
    }
}
