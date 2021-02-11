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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FastGetaway extends Scenario implements Listener {
    public FastGetaway() {
        super(Scenarios.FAST_GETAWAY, new ItemStack(Material.SUGAR));
    }

    public static int duration = 10, level = 2;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onElimination(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null && ev.getKiller().getPlayer().isOnline()) {
            Player k = ev.getKiller().getPlayer().getPlayer();
            k.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, level - 1, true, true));
            k.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§bVous venez de recevoir l'effet §lSpeed " + level + " §bpendant §l" + duration + " §bsecondes.");
        }
    }
}
