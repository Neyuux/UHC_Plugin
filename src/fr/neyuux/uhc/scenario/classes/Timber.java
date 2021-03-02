package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Timber extends Scenario implements Listener {
    public Timber() {
        super(Scenarios.TIMBER, new ItemStack(Material.LOG));
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
    public void onBreak(BlockBreakEvent event) {
        Material mat = event.getBlock().getType();
        if (mat == Material.LOG || mat == Material.LOG_2) {
            final List<Block> bList = new ArrayList<>();

            List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i < bList.size(); i++) {
                        Block block = bList.get(i);
                        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                            List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                        for (BlockFace face : BlockFace.values())
                            if (block.getRelative(face).getType() == Material.LOG
                                    || block.getRelative(face).getType() == Material.LOG_2)
                                bList.add(block.getRelative(face));
                        bList.remove(block);
                    }
                    if (bList.size() == 0) {
                        for(ItemStack item : finalItems)
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0, 0.5), item);
                        cancel();
                    }
                }
            }.runTaskTimer(Index.getInstance(), 1, 1);
        }
    }
}
