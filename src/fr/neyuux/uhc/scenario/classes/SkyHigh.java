package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SkyHigh extends Scenario {
    public SkyHigh() {
        super(Scenarios.SKY_HIGH, new ItemStack(Material.LADDER));
    }

    public static int timer = 60 * 60, highMin = 150, enderpearlGives = 0;
    public static double damage = 0.5;
    public static boolean hasStuffDropOnKiller = false;

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
