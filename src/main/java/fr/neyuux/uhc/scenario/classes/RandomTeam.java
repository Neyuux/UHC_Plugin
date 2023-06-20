package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static fr.neyuux.uhc.GameConfig.ConfigurableParams.SLOTS;
import static fr.neyuux.uhc.GameConfig.ConfigurableParams.TEAMTYPE;

public class RandomTeam extends Scenario {
    public RandomTeam() {
        super(Scenarios.RANDOM_TEAM, new ItemStack(Material.BANNER));
    }

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(GameConfig.getTeamTypeInt((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()), true));
        UHC.getInstance().getUHCTeamManager().clearTeams();
        int p = (int) SLOTS.getValue();
        if (GameConfig.getTeamTypeInt((String)TEAMTYPE.getValue()) > 1) {
            int nt = BigDecimal.valueOf((double) p / GameConfig.getTeamTypeInt((String) TEAMTYPE.getValue())).setScale(0, RoundingMode.UP).toBigInteger().intValue();
            if (nt == 0) nt = 1;
            while (nt != 0) {
                if (nt < 0) throw new IllegalArgumentException("nt est inferieur a 0");
                UHC.getInstance().getUHCTeamManager().createTeam();
                nt--;
            }
        }
        for (Player pl : Bukkit.getOnlinePlayers()) InventoryManager.giveWaitInventory(pl);
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }
}
