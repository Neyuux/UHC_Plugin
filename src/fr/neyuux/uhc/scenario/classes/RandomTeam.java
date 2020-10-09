package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RandomTeam extends Scenario {
    public RandomTeam() {
        super(Scenarios.RANDOM_TEAM, new ItemStack(Material.BANNER));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
