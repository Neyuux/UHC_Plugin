package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AssaultAndBattery extends Scenario {
    public AssaultAndBattery() {
        super(Scenarios.ASSAULT_AND_BATTERY, new ItemStack(Material.BOW));
    }

    public static boolean hasRandomChoice = true;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString().endsWith("To2");
    }
}
