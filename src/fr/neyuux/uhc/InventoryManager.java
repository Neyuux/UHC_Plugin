package fr.neyuux.uhc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryManager {

    private Index main;
    private HashMap<Integer, ItemStack> startInventory = new HashMap<>();
    private HashMap<Integer, ItemStack> startArmor = new HashMap<>();
    private List<ItemStack> deathInventory = new ArrayList<>();
    public InventoryManager(Index main) {
        this.main = main;
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static void giveWaitInventory(Player player) {
        player.getInventory().setItem(1, Index.getSpecTear());
        if (player.hasPermission("uhc.*"))
            player.getInventory().setItem(6, new ItemsStack(Material.REDSTONE_COMPARATOR, "§c§lConfiguration de la partie", "§7Permet de configurer la partie", "§b>>Clique droit").toItemStack());
    }


    public int getStartInventorySize() {
        return startInventory.size() + startArmor.size();
    }

    public int getDeathInventorySize() {
        return deathInventory.size();
    }

}
