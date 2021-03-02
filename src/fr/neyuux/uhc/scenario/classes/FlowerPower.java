package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.Interval;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Random;

import static org.bukkit.Material.*;
import static org.bukkit.Material.WRITTEN_BOOK;

public class FlowerPower extends Scenario implements Listener {
    public FlowerPower() {
        super(Scenarios.FLOWER_POWER, new ItemStack(YELLOW_FLOWER));
    }

    @Override
    protected void activate() {

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
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == YELLOW_FLOWER || e.getBlock().getType() == RED_ROSE || e.getBlock().getType().equals(DOUBLE_PLANT)){
            e.setCancelled(true);
            checkDrop(e.getBlock());
            e.getBlock().setType(AIR);
        }
    }

    private void checkDrop(Block b){
        Material randomMaterial = values()[new Random().nextInt(values().length)];
        if(isAcceptedMaterial(randomMaterial))
            if (isRarity(randomMaterial)) {
                int amount = 1;
                if (randomMaterial == GOLDEN_APPLE || randomMaterial == DIAMOND)
                    amount = new Interval<>(1, 3).getAsRandomInt();
                if (randomMaterial == IRON_INGOT || randomMaterial == GOLD_INGOT)
                    amount = new Interval<>(1, 16).getAsRandomInt();
                ItemStack item = new ItemStack(randomMaterial, amount);
                if (randomMaterial == ENCHANTED_BOOK) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    Random r = new Random();
                    Enchantment en = Enchantment.values()[r.nextInt(Enchantment.values().length)];
                    meta.addStoredEnchant(en, r.nextInt(en.getMaxLevel()) + 1, true);
                    item.setItemMeta(meta);
                }
                b.getWorld().dropItem(b.getLocation(), item);
            } else {
                ItemStack item = new ItemStack(randomMaterial, 16);
                b.getWorld().dropItem(b.getLocation(), item);
            }
        else checkDrop(b);
    }


    private boolean isAcceptedMaterial(Material type){
        return type != AIR && type != WRITTEN_BOOK && type != ARMOR_STAND && type != BEDROCK && type != BED_BLOCK && type != ACACIA_DOOR && type != BIRCH_DOOR_ITEM && type != BURNING_FURNACE && type != CAKE_BLOCK && type != CAULDRON && type != COMMAND && type != COMMAND_MINECART && type != DARK_OAK_DOOR && type != DIODE_BLOCK_OFF && type != DIODE_BLOCK_ON && type != ENDER_PORTAL && type != ENDER_PORTAL_FRAME && type != FLOWER_POT && type != GLOWING_REDSTONE_ORE && type != POWERED_MINECART && type != HOPPER_MINECART && type != PISTON_EXTENSION && type != PISTON_MOVING_PIECE && type != PORTAL && type != STATIONARY_LAVA && type != STATIONARY_WATER && type != SUGAR_CANE_BLOCK && type != WATER && type != LAVA && type != BARRIER && type != MOB_SPAWNER && type != MONSTER_EGG && type != MONSTER_EGGS && type != EXPLOSIVE_MINECART && type != SOIL;
    }

    private boolean isRarity(Material type){
        return type == ANVIL || type == BEACON || type == BREWING_STAND_ITEM || type == CHAINMAIL_BOOTS || type == CHAINMAIL_CHESTPLATE || type == CHAINMAIL_LEGGINGS || type == CHAINMAIL_HELMET || type == LEATHER_BOOTS || type == LEATHER_CHESTPLATE || type == LEATHER_LEGGINGS || type == LEATHER_HELMET
                || type == DIAMOND || type == DIAMOND_AXE || type == DIAMOND_BARDING || type == DIAMOND_BLOCK || type == DIAMOND_BOOTS || type == DIAMOND_CHESTPLATE || type == DIAMOND_HELMET || type == DIAMOND_HOE || type == DIAMOND_LEGGINGS || type == DIAMOND_ORE || type == DIAMOND_PICKAXE || type == DIAMOND_SPADE || type == DIAMOND_SWORD
                || type == IRON_INGOT || type == IRON_AXE || type == IRON_BARDING || type == IRON_BLOCK || type == IRON_BOOTS || type == IRON_CHESTPLATE || type == IRON_HELMET || type == IRON_HOE || type == IRON_LEGGINGS || type == IRON_ORE || type == IRON_PICKAXE || type == IRON_SPADE || type == IRON_SWORD
                || type == GOLD_INGOT || type == GOLD_AXE || type == GOLD_BARDING || type == GOLD_BLOCK || type == GOLD_BOOTS || type == GOLD_CHESTPLATE || type == GOLD_HELMET || type == GOLD_HOE || type == GOLD_LEGGINGS || type == GOLD_ORE || type == GOLD_PICKAXE || type == GOLD_SPADE || type == GOLD_SWORD
                || type == DRAGON_EGG || type == ENDER_PEARL || type == FISHING_ROD || type == LAVA_BUCKET || type == WATER_BUCKET || type == ENCHANTED_BOOK || type == ENCHANTMENT_TABLE || type == RECORD_10 || type == RECORD_11 || type == RECORD_12 || type == RECORD_3 || type == RECORD_4 || type == RECORD_5 || type == RECORD_6 || type == RECORD_7 || type == RECORD_8
                || type == RECORD_9 || type == FLINT_AND_STEEL || type == CAKE || type == STONE_PICKAXE || type == STONE_SPADE || type == STONE_SWORD || type == STONE_HOE || type == STONE_AXE || type == WOOD_PICKAXE || type == WOOD_SPADE || type == WOOD_SWORD || type == WOOD_HOE || type == WOOD_AXE || type == RABBIT_STEW || type == MUSHROOM_SOUP || type == CARROT_STICK
                || type == MILK_BUCKET || type == POTION || type == GOLDEN_APPLE || type == SADDLE || type == BONE || type == INK_SACK || type == BOW || type == BOWL;
    }
}
