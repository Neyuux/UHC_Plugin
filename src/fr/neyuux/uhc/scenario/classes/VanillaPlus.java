package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaPlus extends Scenario {
    public VanillaPlus() {
        super(Scenarios.VANILLA_PLUS, new ItemStack(Material.APPLE));
    }

    public static int apples = 2, flints = 2;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
