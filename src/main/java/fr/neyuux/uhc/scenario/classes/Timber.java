package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
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
            bList.add(event.getBlock());

            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Block block = bList.get(0);
                    addBlock(block, bList);
                    if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                        for (ItemStack it : block.getDrops())
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0, 0.5), it);
                        block.setType(Material.AIR);
                        block.getWorld().playSound(block.getLocation(), Sound.DIG_WOOD, 1f, 1f);
                    }
                    bList.remove(block);
                    if (bList.size() == 0) cancel();
                }
            }.runTaskTimer(UHC.getInstance(), 1, 1);
        }
    }



    private void addBlock(Block b, List<Block> bList) {
        bList.add(b);
        for (BlockFace face : BlockFace.values())
            if (b.getRelative(face).getType() == Material.LOG
                    || b.getRelative(face).getType() == Material.LOG_2)
                if (!bList.contains(b.getRelative(face)))
                    addBlock(b.getRelative(face), bList);
    }
}
