package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RandomTeam extends Scenario {
    public RandomTeam() {
        super(Scenarios.RANDOM_TEAM, new ItemStack(Material.BANNER));
    }

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(GameConfig.getTeamTypeInt((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()), true));
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }
}
