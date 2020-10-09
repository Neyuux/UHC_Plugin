package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Switch extends Scenario {
    public Switch() {
        super(Scenarios.SWITCH, new ItemStack(Material.ENDER_PEARL));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
