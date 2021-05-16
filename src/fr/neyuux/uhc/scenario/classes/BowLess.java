package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class BowLess extends Scenario implements Listener {
    public BowLess() {
        super(Scenarios.BOW_LESS, new ItemStack(Material.BOW));
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
    public void onCraft(CraftItemEvent ev) {
        if (ev.getInventory().getResult().getType().equals(Material.BOW)) {
            ev.setCancelled(true);
            ev.getWhoClicked().sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLe craft de l'arc est désactivé.");
            UHC.playNegativeSound((Player)ev.getWhoClicked());
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent ev) {
        if(ev.getItem().getItemStack().getType().equals(Material.BOW)) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cL'arc est désactivé.");
            UHC.playNegativeSound(ev.getPlayer());
        }
    }

    @EventHandler
    public void onThrowArrow(ProjectileLaunchEvent ev) {
        if (ev.getEntity().getType().equals(EntityType.ARROW) && ev.getEntity().getShooter() instanceof Player) {
            Player p = (Player) ev.getEntity().getShooter();
            ev.setCancelled(true);
            p.sendMessage(UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLe craft de l'arc est désactivé.");
            UHC.playNegativeSound(p);
        }
    }
}
