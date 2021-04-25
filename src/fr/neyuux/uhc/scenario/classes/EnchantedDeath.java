package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class EnchantedDeath extends Scenario implements Listener {
    public EnchantedDeath() {
        super(Scenarios.ENCHANTED_DEATH, new ItemStack(Material.ENCHANTMENT_TABLE));
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
    public void onDeath(PlayerEliminationEvent ev) {
        ArrayList<ItemStack> l = new ArrayList<>(Arrays.asList(ev.getPlayerUHC().getLastInv()));
        l.add(new ItemStack(Material.ENCHANTMENT_TABLE));
        ev.getPlayerUHC().setLastInv(l.toArray(new ItemStack[0]));
    }

}
