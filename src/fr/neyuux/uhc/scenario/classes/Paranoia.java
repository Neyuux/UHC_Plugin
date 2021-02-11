package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Paranoia extends Scenario {
    public Paranoia() {
        super(Scenarios.PARANOIA, new ItemStack(Material.PAPER));
    }

    public static boolean hasMineDiamond = false, hasMineGold = false, hasCraftEnchant = false, hasCraftAnvil = false, hasCraftGoldenApple = false,
            hasCraftGoldenHead = false, hasDeath = false, hasPortalTravel = false, hasUseGoldenApple = false, hasUseGoldenHead = false;

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
