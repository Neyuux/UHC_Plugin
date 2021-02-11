package fr.neyuux.uhc.scenario.classes;

import com.google.common.collect.Lists;
import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.Interval;
import fr.neyuux.uhc.util.Loot;
import fr.neyuux.uhc.util.LootItem;
import fr.neyuux.uhc.util.VarsLoot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CutClean extends Scenario implements Listener {
    public CutClean() {
        super(Scenarios.CUT_CLEAN, new ItemStack(Material.COAL));
    }

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());

        VarsLoot.getBlocksLoots().put(Material.GOLD_ORE, new Loot(1.2, Lists.newArrayList(
                new LootItem(new ItemStack(Material.GOLD_INGOT, 1), 100.0, new Interval<>(1, 1)))));

        VarsLoot.getBlocksLoots().put(Material.IRON_ORE, new Loot(1.5, Lists.newArrayList(
                new LootItem(new ItemStack(Material.IRON_INGOT, 1), 100.0, new Interval<>(1, 1)))));
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Entity entity = e.getEntity();
        if (entity.getType() == EntityType.CHICKEN) for (ItemStack drops : e.getDrops()) {
            if (drops.getType() == Material.RAW_CHICKEN) drops.setType(Material.COOKED_CHICKEN);
        }
        if (entity.getType() == EntityType.SHEEP) for (ItemStack drops : e.getDrops()) {
            if (drops.getType() == Material.MUTTON) drops.setType(Material.COOKED_MUTTON);
        }
        if (entity.getType() == EntityType.RABBIT) for (ItemStack drops : e.getDrops()) {
            if (drops.getType() == Material.RABBIT) drops.setType(Material.COOKED_RABBIT);
        }
        if ((entity.getType() == EntityType.COW) || (entity.getType() == EntityType.MUSHROOM_COW)) {
            for (ItemStack drops : e.getDrops())
                if (drops.getType() == Material.RAW_BEEF) drops.setType(Material.COOKED_BEEF);
        }
        if (entity.getType() == EntityType.PIG) for (ItemStack drops : e.getDrops())
            if (drops.getType() == Material.PORK) drops.setType(Material.GRILLED_PORK);
    }
}
