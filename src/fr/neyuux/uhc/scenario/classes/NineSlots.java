package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NineSlots extends Scenario {
    public NineSlots() {
        super(Scenarios.NINE_SLOTS, new ItemStack(Material.STAINED_GLASS, 1, (short)7));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
