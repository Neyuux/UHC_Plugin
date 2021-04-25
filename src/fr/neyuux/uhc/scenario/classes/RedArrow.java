package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class RedArrow extends Scenario implements Listener {
    public RedArrow() {
        super(Scenarios.RED_ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onDeath(PlayerEliminationEvent e) {
        PlayerUHC pu = e.getPlayerUHC();

        createRedArrow(pu.getLastLocation().getWorld().getHighestBlockAt(pu.getLastLocation()).getLocation().add(0, 30, 0), pu);
    }

    public static void createRedArrow(Location loc, PlayerUHC pu) {
        changeRedWool(loc.getBlock(), pu);

        int x = loc.getBlockX(); int y = loc.getBlockY();
        int z = loc.getBlockZ(); World w = loc.getWorld();

        changeRedWool(new Location(w, x, y + 1, z).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 1, z+1).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 1, z-1).getBlock(), pu);

        for(int i = 0; i < 18; i++) {
            changeRedWool(new Location(w, x, y + 2 + i, z).getBlock(), pu);
            changeRedWool(new Location(w, x, y + 2 + i, z+1).getBlock(), pu);
            changeRedWool(new Location(w, x, y + 2 + i, z+2).getBlock(), pu);
            changeRedWool(new Location(w, x, y + 2 + i, z-1).getBlock(), pu);
            changeRedWool(new Location(w, x, y + 2 + i, z-2).getBlock(), pu);
        }

        changeRedWool(new Location(w, x, y + 3, z+3).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 3, z-3).getBlock(), pu);

        changeRedWool(new Location(w, x, y + 4, z+3).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 4, z-3).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 4, z+4).getBlock(), pu);
        changeRedWool(new Location(w, x, y + 4, z-4).getBlock(), pu);
    }

    private static void changeRedWool(Block block, PlayerUHC pu) {
        block.setType(Material.WOOL);
        BlockState state = block.getState();
        DyeColor dc = DyeColor.RED;
        if (pu.getTeam() != null) dc = pu.getTeam().getPrefix().color.getDyecolor();
        state.setData(new Wool(dc));
        state.update();
    }
}
