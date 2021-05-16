package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.Loot;
import fr.neyuux.uhc.util.LootItem;
import fr.neyuux.uhc.util.VarsLoot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VeinMiner extends Scenario implements Listener {
    public VeinMiner() {
        super(Scenarios.VEINMINER, new ItemStack(Material.COAL_ORE));
    }

    private final List<Block> check = new ArrayList<>();

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


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock() == null) return;

        if(e.getBlock().getType() == Material.DIAMOND_ORE || e.getBlock().getType() == Material.GOLD_ORE || e.getBlock().getType() == Material.IRON_ORE
                || e.getBlock().getType() == Material.GLOWING_REDSTONE_ORE || e.getBlock().getType() == Material.REDSTONE_ORE || e.getBlock().getType() == Material.COAL_ORE || e.getBlock().getType() == Material.LAPIS_ORE){

            List<Block> filon = new ArrayList<>();
            if(!check.contains(e.getBlock())){
                Material type = e.getBlock().getType();
                if(type == Material.GLOWING_REDSTONE_ORE)
                    type = Material.REDSTONE_ORE;

                countBlocks(type, e.getBlock(), filon);
                destroyBlocks(e.getPlayer(), filon, e);
            }
        }
    }


    private void countBlocks(Material type, Block b, List<Block> filon){
        Material btype = b.getType();
        if (btype.equals(Material.GLOWING_REDSTONE_ORE)) btype = Material.REDSTONE_ORE;
        if(btype != type) return;
        if(filon.contains(b)) return;

        filon.add(b);
        check.add(b);

        for (BlockFace blockFace : BlockFace.values()) countBlocks(type, b.getRelative(blockFace), filon);
    }

    private void destroyBlocks(Player p, List<Block> filon, BlockBreakEvent e){
        for(Block b : filon)
            if (b.getType() != Material.AIR) if (checkBlock(b, e, filon.size())) {
                e.setCancelled(true);
                PlayerUHC up = UHC.getInstance().getPlayerUHC(p);
                if(b.getType() == Material.DIAMOND_ORE) up.addDiamonds(1);
                else if(b.getType() == Material.GOLD_ORE) up.addGolds(1);
                else if(b.getType() == Material.IRON_ORE) up.addIrons(1);
                b.setType(Material.AIR);
                if (b != e.getBlock()) Bukkit.getPluginManager().callEvent(new BlockBreakEvent(b, p));
                ItemStack item = p.getItemInHand();
                if (item != null && item.getType() != Material.AIR && item.getType().getMaxDurability() > 0) {
                    if (item.getDurability() + 1 >= item.getType().getMaxDurability()) {
                        p.getInventory().removeItem(item);
                        p.updateInventory();
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    } else
                        item.setDurability((short) (item.getDurability() + 1));
                }
            }
        check.clear();
    }

    private boolean checkBlock(Block b, BlockBreakEvent event, int size) {
        Material mat = b.getType();
        HashMap<Material, Loot> loots = VarsLoot.getBlocksLoots();
        boolean ol = true;
        if (Scenarios.ORE_LIMITER.isActivated()) {
            PlayerUHC pu = UHC.getInstance().getPlayerUHC(event.getPlayer());
            if (mat.equals(Material.GOLD_ORE) && pu.getGolds() >= OreLimiter.golds) ol = false;
            if (mat.equals(Material.DIAMOND_ORE) && pu.getDiamonds() >= OreLimiter.diamonds) ol = false;
            if (mat.equals(Material.IRON_ORE) && pu.getIrons() >= OreLimiter.irons) ol = false;
        }

        if (loots.containsKey(mat) && ol) {
            Loot loot = loots.get(mat);
            World w = b.getLocation().getWorld();
            if (loot.getExp() != 0 && event != null) {
                ExperienceOrb orb = w.spawn(b.getLocation(), ExperienceOrb.class);
                int exp = (int) (event.getExpToDrop() == 0 ? loot.getExp() : loot.getExp() * event.getExpToDrop());
                orb.setExperience(exp*(size-1));
            }
            for (LootItem item : loot.getLoots()) {
                ItemStack is = item.getLootItem();
                if (is != null) w.dropItemNaturally(b.getLocation().add(0.5, 0, 0.5), is);
            }
            return true;
        }
        return false;
    }


}
