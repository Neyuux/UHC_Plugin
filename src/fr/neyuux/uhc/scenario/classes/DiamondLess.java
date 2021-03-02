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
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class DiamondLess extends Scenario implements Listener {
    public DiamondLess() {
        super(Scenarios.DIAMOND_LESS, new ItemStack(Material.DIAMOND_ORE));
    }

    @Override
    protected void activate() {
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);

        VarsLoot.getBlocksLoots().put(Material.DIAMOND_ORE, new Loot(0, Lists.newArrayList(
                new LootItem(new ItemStack(Material.COBBLESTONE), 100.0, new Interval<>(1, 1)))));
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler(priority= EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.DIAMOND_ORE)
            Index.sendActionBar(e.getPlayer(), Index.getStaticPrefix() + scenario.getDisplayName() + " §cDiamants désactivés !");
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent e){
        if (e.getEntity() != null && e.getEntity().getType() == EntityType.PRIMED_TNT) {
            e.setCancelled(true);
            Iterator<Block> iter = e.blockList().iterator();
            while (iter.hasNext()) {
                Block b = iter.next();
                if (b.getType() == Material.DIAMOND_ORE) {
                    iter.remove();
                    b.setType(Material.AIR);
                }
            }
        }
    }
}
