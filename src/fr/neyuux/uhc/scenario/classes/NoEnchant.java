package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoEnchant extends Scenario {
    public NoEnchant() {
        super(Scenarios.NO_ENCHANT, new ItemStack(Material.ENCHANTMENT_TABLE));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
