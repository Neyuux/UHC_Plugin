package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MasterLevel extends Scenario {
    public MasterLevel() {
        super(Scenarios.MASTER_LEVEL, new ItemStack(Material.EXP_BOTTLE));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
