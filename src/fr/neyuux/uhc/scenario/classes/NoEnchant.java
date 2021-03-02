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
import org.bukkit.inventory.ItemStack;

public class NoEnchant extends Scenario implements Listener {
    public NoEnchant() {
        super(Scenarios.NO_ENCHANT, new ItemStack(Material.ENCHANTMENT_TABLE));
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
        if (ev.getInventory().getResult().getType().equals(Material.ENCHANTMENT_TABLE)) {
            ev.setCancelled(true);
            ev.getWhoClicked().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLe craft de la table d'enchantement est désactivé.");
            Index.playNegativeSound((Player)ev.getWhoClicked());
        }
    }


    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent ev) {
        ev.setCancelled(true);
        ev.getEnchanter().closeInventory();
        ev.getEnchanter().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §cLa table d'enchantement est désactivée.");
        Index.playNegativeSound(ev.getEnchanter());
    }
}
