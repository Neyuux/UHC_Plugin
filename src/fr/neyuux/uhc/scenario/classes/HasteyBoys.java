package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoys extends Scenario implements Listener {
    public HasteyBoys() {
        super(Scenarios.HASTEY_BOYS, new ItemStack(Material.GOLD_PICKAXE));
    }

    public static int enchantLevel = 3;

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        for (PlayerUHC pu : UHC.getInstance().getAlivePlayers())
            if (pu.getPlayer().isOnline())
                for (ItemStack it : pu.getPlayer().getPlayer().getInventory().getContents())
                    if (isUpgradable(it.getType())) enchantItem(it);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (isUpgradable(e.getCurrentItem().getType()))
            e.setCurrentItem(enchantItem(e.getCurrentItem()));
    }

    public Boolean isUpgradable(Material type) {
        return type == Material.WOOD_AXE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE
                || type == Material.GOLD_AXE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE
                || type == Material.STONE_AXE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE
                || type == Material.IRON_AXE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE
                || type == Material.DIAMOND_AXE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SPADE;
    }

    public ItemStack enchantItem(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, enchantLevel);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
        return item;
    }
}
