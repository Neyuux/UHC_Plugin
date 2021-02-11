package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoFall extends Scenario {
    public NoFall() {
        super(Scenarios.NO_FALL, new ItemStack(Material.GOLD_BOOTS));
    }

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
