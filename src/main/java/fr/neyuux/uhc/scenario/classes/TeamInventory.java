package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TeamInventory extends Scenario implements Listener {
    public TeamInventory() {
        super(Scenarios.TEAM_INVENTORY, new ItemStack(Material.CHEST));
    }

    public static final HashMap<UHCTeam, Inventory> inventories = new HashMap<>();

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        inventories.clear();
        for (UHCTeam t : UHC.getInstance().getUHCTeamManager().getAliveTeams())
            inventories.put(t, Bukkit.createInventory(null, 27, scenario.getDisplayName()));
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    @EventHandler
    public void onElim(PlayerEliminationEvent ev) {
        if (ev.getPlayerUHC().getTeam().getAlivePlayers().size() == 1) {
            Block b = ev.getStuffLocation().add(0, 3 ,0).getBlock();
            b.setType(Material.CHEST);
            Chest chest = (Chest) b.getState();
            chest.getBlockInventory().setContents(inventories.get(ev.getPlayerUHC().getTeam()).getContents());
            ((CraftChest) chest).getTileEntity().a(scenario.getDisplayName() + "§8§l " + Symbols.DOUBLE_ARROW + " " + ev.getPlayerUHC().getTeam().getTeam().getDisplayName());
        }
    }
}
