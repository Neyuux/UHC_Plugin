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
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class NoBookShelves extends Scenario implements Listener {
    public NoBookShelves() {
        super(Scenarios.NO_BOOK_SHELVES, new ItemStack(Material.BOOKSHELF));
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
    public void onCraft(CraftItemEvent ev) {
        if (ev.getInventory().getResult().getType().equals(Material.BOOKSHELF)) {
            ev.setCancelled(true);
            ev.getWhoClicked().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLe craft de la bibliothèque est désactivé.");
            Index.playNegativeSound((Player)ev.getWhoClicked());
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent ev) {
        if(ev.getItem().getItemStack().getType().equals(Material.BOOKSHELF)) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLes bibliothèques est désactivées.");
            Index.playNegativeSound(ev.getPlayer());
        }
    }

    @EventHandler
    public void onEnchantWithBookshelves(PrepareItemEnchantEvent ev) {
        if (ev.getEnchantmentBonus() != 0) {
            ev.setCancelled(true);
            ev.getEnchanter().closeInventory();
            ev.getEnchanter().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLes bibliothèques est désactivées.");
            Index.playNegativeSound(ev.getEnchanter());
        }
    }
}
