package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PotionLimiter extends Scenario {
    public PotionLimiter() {
        super(Scenarios.POTION_LIMITER, new ItemStack(Material.BREWING_STAND_ITEM));
    }

    public static boolean hasPotions = true, hasLevel2Potions = true, hasExtendedPotions = true, hasSplash = true,
        hasStrength = true, hasSpeed = true, hasNightVision = true, hasJumpBoost = true, hasFireResistance = true,
        hasWaterBreathing = true, hasHeal = true, hasPoison = true, hasRegeneration = true;

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
