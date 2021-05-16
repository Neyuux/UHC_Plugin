package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class BloodEnchant extends Scenario implements Listener {
    public BloodEnchant() {
        super(Scenarios.BLOOD_ENCHANT, new ItemStack(Material.ENCHANTMENT_TABLE));
    }

    public static double damage = 0.5;

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
    public void onEnchant(EnchantItemEvent ev) {
        PlayerUHC pu = UHC.getInstance().getPlayerUHC(ev.getEnchanter());
        if (pu.isAlive()) {
            ev.getEnchanter().damage(0);
            if (ev.getEnchanter().getHealth() > damage * 2.0) ev.getEnchanter().setHealth(ev.getEnchanter().getHealth() - damage * 2.0);
            else new FightListener(UHC.getInstance()).eliminate(ev.getEnchanter(), true, null, ev.getEnchanter().getDisplayName() + " §da trop enchanté.");
            UHC.sendActionBar(ev.getEnchanter(), UHC.getPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§4Vous perdez " + damage + Symbols.HEARTH + "  en enchantant.");
        }
    }
}
