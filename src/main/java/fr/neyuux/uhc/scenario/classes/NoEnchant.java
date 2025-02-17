package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
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
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
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
            ev.getWhoClicked().sendMessage(getPrefix() + "�cLe craft de la table d'enchantement est d�sactiv�.");
            UHC.playNegativeSound((Player)ev.getWhoClicked());
        }
    }


    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent ev) {
        ev.setCancelled(true);
        ev.getEnchanter().closeInventory();
        ev.getEnchanter().sendMessage(getPrefix() + "�cLa table d'enchantement est d�sactiv�e.");
        UHC.playNegativeSound(ev.getEnchanter());
    }
}
