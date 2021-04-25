package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static fr.neyuux.uhc.config.GameConfig.ConfigurableParams.SLOTS;
import static fr.neyuux.uhc.config.GameConfig.ConfigurableParams.TEAMTYPE;

public class RandomTeam extends Scenario {
    public RandomTeam() {
        super(Scenarios.RANDOM_TEAM, new ItemStack(Material.BANNER));
    }

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(GameConfig.getTeamTypeInt((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()), true));
        Index.getInstance().getUHCTeamManager().clearTeams();
        int p = (int) SLOTS.getValue();
        if (GameConfig.getTeamTypeInt((String)TEAMTYPE.getValue()) > 1) {
            int nt = BigDecimal.valueOf((double) p / GameConfig.getTeamTypeInt((String) TEAMTYPE.getValue())).setScale(0, RoundingMode.UP).toBigInteger().intValue();
            if (nt == 0) nt = 1;
            while (nt != 0) {
                if (nt < 0) throw new IllegalArgumentException("nt est inferieur a 0");
                Index.getInstance().getUHCTeamManager().createTeam();
                nt--;
            }
        }
        for (PlayerUHC pu : Index.getInstance().players) InventoryManager.giveWaitInventory(pu);
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }
}
