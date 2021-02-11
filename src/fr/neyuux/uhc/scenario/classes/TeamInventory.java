package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TeamInventory extends Scenario {
    public TeamInventory() {
        super(Scenarios.TEAM_INVENTORY, new ItemStack(Material.CHEST));
    }

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
