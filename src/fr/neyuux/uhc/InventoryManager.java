package fr.neyuux.uhc;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class InventoryManager {

    private Index main;
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
        player.getInventory().setItem(1, Index.getItem(Material.GHAST_TEAR, 1, Arrays.asList("§7Permet de devenir spectateur", "§b>>Clique droit"), "§7§lDevenir Spectateur", (short)0));
        if (player.hasPermission("uhc.*"))
            player.getInventory().setItem(6, Index.getItem(Material.REDSTONE_COMPARATOR, 1, Arrays.asList("§7Permet de configurer la partie", "§b>>Clique droit"), "§c§lConfiguration de la partie", (short)0));
    }

}
