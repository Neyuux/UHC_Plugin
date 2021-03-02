package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class RewardingLongShot extends Scenario implements Listener {
    public RewardingLongShot() {
        super(Scenarios.REWARDING_LONGSHOT, new ItemStack(Material.GOLD_NUGGET));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);

        Bukkit.broadcastMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §bPrix par Longshots : ");
        Bukkit.broadcastMessage("§e30-49 Blocks > 1 Lingot de fer");
        Bukkit.broadcastMessage("§e50-69 Blocks > 1 Lingot de fer + 1 Lingot d'or");
        Bukkit.broadcastMessage("§e70-150 Blocks > 1 Diamant + 2 Lingots de fer + 2 Lingots d'or");
        Bukkit.broadcastMessage("§e+150 Blocks > 5 Diamant + 2 Lingots de fer + 3 Lingots d'or");
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onShoot(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Arrow)) return;
        if(!(e.getEntity() instanceof Player)) return;
        Arrow a = (Arrow) e.getDamager();
        if(!(a.getShooter() instanceof Player)) return;

        Player shooter = (Player) a.getShooter();
        Location lp = e.getEntity().getLocation();
        Location ls = shooter.getLocation();
        double distance = ls.distance(lp);

        if (distance >= 30 && distance < 49) {
            InventoryManager.give(shooter, null, new ItemStack(Material.IRON_INGOT, 1));
            shooter.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§bVous avez touché à " + distance + " blocks ! Vous obtenez donc 1 lingot de fer.");
        } else if (distance >= 50 && distance < 70) {
            InventoryManager.give(shooter, null, new ItemStack(Material.IRON_INGOT, 1));
            InventoryManager.give(shooter, null, new ItemStack(Material.GOLD_INGOT, 1));
            shooter.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§bVous avez touché à " + distance + " blocks ! Vous obtenez donc 1 lingot de fer et 1 lingot d'or.");
        } else if (distance >= 70 && distance < 150) {
            InventoryManager.give(shooter, null, new ItemStack(Material.DIAMOND, 1));
            InventoryManager.give(shooter, null, new ItemStack(Material.GOLD_INGOT, 2));
            InventoryManager.give(shooter, null, new ItemStack(Material.IRON_INGOT, 2));
            shooter.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§bVous avez touché à " + distance + " blocks ! Vous obtenez donc 2 lingots de fer, 2 lingots d'or et 1 diamant.");
        } else if (distance >= 150) {
            InventoryManager.give(shooter, null, new ItemStack(Material.DIAMOND, 5));
            InventoryManager.give(shooter, null, new ItemStack(Material.GOLD_INGOT, 3));
            InventoryManager.give(shooter, null, new ItemStack(Material.IRON_INGOT, 2));
            shooter.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§bVous avez touché à " + distance + " blocks ! Vous obtenez donc 2 lingots de fer, 3 lingots d'or et 5 diamants.");
        }
    }
}
