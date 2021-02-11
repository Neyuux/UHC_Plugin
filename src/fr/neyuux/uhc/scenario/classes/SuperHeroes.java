package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SuperHeroes extends Scenario {
    public SuperHeroes() {
        super(Scenarios.SUPER_HEROES, new ItemStack(Material.NETHER_STAR));
    }

    public static boolean hasResistance = true, hasStrength = true, hasSpeed = true, hasJumpBoost = true, hasDoubleHealth = true;

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
