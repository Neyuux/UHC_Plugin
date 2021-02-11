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

public class FlowerPower extends Scenario implements Listener {
    public FlowerPower() {
        super(Scenarios.FLOWER_POWER, new ItemStack(Material.YELLOW_FLOWER));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.YELLOW_FLOWER || e.getBlock().getType() == Material.RED_ROSE){
            e.setCancelled(true);
            checkDrop(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

    private void checkDrop(Block b){
        Material randomMaterial = Material.values()[new Random().nextInt(Material.values().length)];
        if(isAcceptedMaterial(randomMaterial))
            if (isRarity(randomMaterial)) {
                int amount = 1;
                if (randomMaterial == Material.GOLDEN_APPLE || randomMaterial == Material.DIAMOND)
                    amount = new Interval<>(1, 3).getAsRandomInt();
                if (randomMaterial == Material.IRON_INGOT || randomMaterial == Material.GOLD_INGOT)
                    amount = new Interval<>(1, 16).getAsRandomInt();
                ItemStack item = new ItemStack(randomMaterial, amount);
                if (randomMaterial == Material.ENCHANTED_BOOK) {
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
        return type != Material.AIR && type != Material.ARMOR_STAND && type != Material.BEDROCK && type != Material.BED_BLOCK && type != Material.ACACIA_DOOR && type != Material.BIRCH_DOOR_ITEM && type != Material.BURNING_FURNACE && type != Material.CAKE_BLOCK && type != Material.CAULDRON && type != Material.COMMAND && type != Material.COMMAND_MINECART && type != Material.DARK_OAK_DOOR && type != Material.DIODE_BLOCK_OFF && type != Material.DIODE_BLOCK_ON && type != Material.ENDER_PORTAL && type != Material.ENDER_PORTAL_FRAME && type != Material.FLOWER_POT && type != Material.GLOWING_REDSTONE_ORE && type != Material.POWERED_MINECART && type != Material.HOPPER_MINECART && type != Material.PISTON_EXTENSION && type != Material.PISTON_MOVING_PIECE && type != Material.PORTAL && type != Material.STATIONARY_LAVA && type != Material.STATIONARY_WATER && type != Material.SUGAR_CANE_BLOCK && type != Material.WATER && type != Material.LAVA && type != Material.BARRIER && type != Material.MOB_SPAWNER && type != Material.MONSTER_EGG && type != Material.MONSTER_EGGS && type != Material.EXPLOSIVE_MINECART && type != Material.SOIL;
    }

    private boolean isRarity(Material type){
        return type == Material.ANVIL || type == Material.BEACON || type == Material.BREWING_STAND_ITEM || type == Material.CHAINMAIL_BOOTS || type == Material.CHAINMAIL_CHESTPLATE || type == Material.CHAINMAIL_LEGGINGS || type == Material.CHAINMAIL_HELMET || type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_HELMET
                || type == Material.DIAMOND || type == Material.DIAMOND_AXE || type == Material.DIAMOND_BARDING || type == Material.DIAMOND_BLOCK || type == Material.DIAMOND_BOOTS || type == Material.DIAMOND_CHESTPLATE || type == Material.DIAMOND_HELMET || type == Material.DIAMOND_HOE || type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_ORE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SPADE || type == Material.DIAMOND_SWORD
                || type == Material.IRON_INGOT || type == Material.IRON_AXE || type == Material.IRON_BARDING || type == Material.IRON_BLOCK || type == Material.IRON_BOOTS || type == Material.IRON_CHESTPLATE || type == Material.IRON_HELMET || type == Material.IRON_HOE || type == Material.IRON_LEGGINGS || type == Material.IRON_ORE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE || type == Material.IRON_SWORD
                || type == Material.GOLD_INGOT || type == Material.GOLD_AXE || type == Material.GOLD_BARDING || type == Material.GOLD_BLOCK || type == Material.GOLD_BOOTS || type == Material.GOLD_CHESTPLATE || type == Material.GOLD_HELMET || type == Material.GOLD_HOE || type == Material.GOLD_LEGGINGS || type == Material.GOLD_ORE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE || type == Material.GOLD_SWORD
                || type == Material.DRAGON_EGG || type == Material.ENDER_PEARL || type == Material.FISHING_ROD || type == Material.LAVA_BUCKET || type == Material.WATER_BUCKET || type == Material.ENCHANTED_BOOK || type == Material.ENCHANTMENT_TABLE || type == Material.RECORD_10 || type == Material.RECORD_11 || type == Material.RECORD_12 || type == Material.RECORD_3 || type == Material.RECORD_4 || type == Material.RECORD_5 || type == Material.RECORD_6 || type == Material.RECORD_7 || type == Material.RECORD_8
                || type == Material.RECORD_9 || type == Material.FLINT_AND_STEEL || type == Material.CAKE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE || type == Material.STONE_SWORD || type == Material.STONE_HOE || type == Material.STONE_AXE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_SWORD || type == Material.WOOD_HOE || type == Material.WOOD_AXE || type == Material.RABBIT_STEW || type == Material.MUSHROOM_SOUP || type == Material.CARROT_STICK
                || type == Material.MILK_BUCKET || type == Material.POTION || type == Material.GOLDEN_APPLE || type == Material.SADDLE || type == Material.BONE || type == Material.INK_SACK || type == Material.BOW || type == Material.BOWL;
    }
}
