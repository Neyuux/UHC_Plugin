package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.ENDER_PEARL;

public class Switch extends Scenario {
    public Switch() {
        super(Scenarios.SWITCH, new ItemStack(ENDER_PEARL));
    }

    public static int firstSwitch = 20 * 60, switchFrequency = 15 * 60, randomTimeLimit = 0;
    public static boolean hasInvSwitch = false, hasSoloSwitch = true, hasTeamBalancing = false;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }

}
