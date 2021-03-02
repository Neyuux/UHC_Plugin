package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TeamInventory extends Scenario {
    public TeamInventory() {
        super(Scenarios.TEAM_INVENTORY, new ItemStack(Material.CHEST));
    }

    public static final HashMap<UHCTeam, Inventory> inventories = new HashMap<>();

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        inventories.clear();
        for (UHCTeam t : Index.getInstance().getUHCTeamManager().getAliveTeams())
            inventories.put(t, Bukkit.createInventory(null, 27, scenario.getDisplayName()));
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }
}
