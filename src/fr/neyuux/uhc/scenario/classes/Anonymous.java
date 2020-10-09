package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.ItemsStack;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Anonymous extends Scenario {
    public Anonymous() {
        super(Scenarios.ANONYMOUS, new ItemsStack(new ItemStack(Material.SKULL_ITEM, 1, (short)3)).toItemStackwithMinecraftHeadsValueMeta("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRiNTRmN2Y1NTkzYTMyM2I2NTUyMWU2MTA2MTZmZGM5OTEwZjI5ZTI3YWUzMTkxNTExNjIzZTgxOGQ4ODM0OCJ9fX0="));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }
}
