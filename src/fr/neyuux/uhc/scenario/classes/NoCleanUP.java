package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoCleanUP extends Scenario {
    public NoCleanUP() {
        super(Scenarios.NO_CLEANUP, new ItemStack(Material.IRON_SWORD));
    }

    public static double healthAdded = 3.0;

    @Override
    public void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
