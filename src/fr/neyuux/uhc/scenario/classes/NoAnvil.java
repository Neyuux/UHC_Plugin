package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class NoAnvil extends Scenario implements Listener {
    public NoAnvil() {
        super(Scenarios.NO_ANVIL, new ItemStack(Material.ANVIL));
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
    public void onCraft(CraftItemEvent ev) {
        if (ev.getInventory().getResult().getType().equals(Material.ANVIL)) {
            ev.setCancelled(true);
            ev.getWhoClicked().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLe craft de l'enclume est désactivé.");
            Index.playNegativeSound((Player)ev.getWhoClicked());
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent ev) {
        if(ev.getItem().getItemStack().getType().equals(Material.ANVIL)) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cL'enclume est désactivée.");
            Index.playNegativeSound(ev.getPlayer());
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() instanceof AnvilInventory) {
            p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cL'utilisation de l'enclume est désactivée.");
            Index.playNegativeSound(p);
            e.setCancelled(true);
            p.closeInventory();
        }
    }
}
