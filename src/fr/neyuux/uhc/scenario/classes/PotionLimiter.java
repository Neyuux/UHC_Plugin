package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PotionLimiter extends Scenario {
    public PotionLimiter() {
        super(Scenarios.POTION_LIMITER, new ItemStack(Material.BREWING_STAND_ITEM));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
