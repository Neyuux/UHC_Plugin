package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class NoCleanUP extends Scenario implements Listener {
    public NoCleanUP() {
        super(Scenarios.NO_CLEANUP, new ItemStack(Material.IRON_SWORD));
    }

    public static double healthAdded = 3.0;

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
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null) {
            PlayerUHC ku = ev.getKiller();
            double a = healthAdded * 2.0;
            if (ku.getPlayer().isOnline()) {
                Player k = ku.getPlayer().getPlayer();
                k.setHealth(Math.min((k.getHealth() + a), k.getMaxHealth()));
                k.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§dVous venez de récupérer §4§l" +  healthAdded + Symbols.HEARTH + " §d!");
            } else ku.health = Math.min((ku.health + a), ku.maxHealth);
        }
    }
}
