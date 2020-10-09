package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TimeBomb extends Scenario {
    public TimeBomb() {
        super(Scenarios.TIME_BOMB, new ItemStack(Material.TRAPPED_CHEST));
    }

    @Override
    public void activate() {

    }

    @Override
    public void execute() {

    }
}
