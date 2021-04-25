package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.metadata.FixedMetadataValue;

public class GraveRobbers extends Scenario implements Listener {
    public GraveRobbers() {
        super(Scenarios.GRAVE_ROBBERS, new ItemStack(Material.GRAVEL));
    }

    @Override
    protected void activate() {
        if ((boolean) GameConfig.ConfigurableParams.BARRIER_HEAD.getValue())Index.sendHostMessage(Index.getStaticPrefix() + "§cVeuillez désactiver l'apparition d'un poteau au kill pour que " + scenario.getDisplayName() + " §cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return !(boolean)GameConfig.ConfigurableParams.BARRIER_HEAD.getValue();
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) e.getBlock().getState();
            if (chest.hasMetadata("nobreak")) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        if (e.getEntity() != null && e.getEntity().getType() == EntityType.PRIMED_TNT) {
            e.setCancelled(true);
            e.blockList().removeIf(b -> b.getType() == Material.CHEST && b.getState().hasMetadata("nobreak"));
        }
    }

    @EventHandler
    public void onDied(PlayerEliminationEvent e) {
        setupGraveRobbers(e.getPlayerUHC());
    }

    public static void setupGraveRobbers(PlayerUHC p) {
        Location sol = getLocationInGround(p.getLastLocation());
        int x = sol.getBlockX(); int y = sol.getBlockY() - 1;
        int z = sol.getBlockZ(); World w = sol.getWorld();

        // CROIX
        new Location(w, x, y+1, z-1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x, y+2, z-1).getBlock().setType(Material.SMOOTH_BRICK);

        Location signLoc = new Location(w, x, y+2, z);
        signLoc.getBlock().setType(Material.WALL_SIGN);
        Sign sign = (Sign) signLoc.getBlock().getState();
        ((Directional) sign.getData()).setFacingDirection(BlockFace.SOUTH);
        sign.setLine(0, "----------");
        sign.setLine(1, "R.I.P");
        sign.setLine(2, p.getPlayer().getName().length() > 16 ? p.getPlayer().getName().substring(0, 16) : p.getPlayer().getName());
        sign.setLine(3, "----------");
        sign.update();

        new Location(w, x+1, y+2, z-1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x-1, y+2, z-1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x, y+3, z-1).getBlock().setType(Material.SMOOTH_BRICK);

        // SOL
        new Location(w, x, y, z).getBlock().setType(Material.GRAVEL);
        new Location(w, x, y, z-1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x+1, y, z).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x-1, y, z).getBlock().setType(Material.COBBLESTONE);

        new Location(w, x, y, z+1).getBlock().setType(Material.GRAVEL);
        new Location(w, x+1, y, z+1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x-1, y, z+1).getBlock().setType(Material.COBBLESTONE);

        // SOL - 1
        Location chestLoc = new Location(w, x, y-1, z);
        chestLoc.getBlock().setType(Material.CHEST);
        new Location(w, x+1, y-1, z).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x-1, y-1, z).getBlock().setType(Material.COBBLESTONE);

        new Location(w, x, y-1, z+1).getBlock().setType(Material.CHEST);
        new Location(w, x+1, y-1, z+1).getBlock().setType(Material.COBBLESTONE);
        new Location(w, x-1, y-1, z+1).getBlock().setType(Material.COBBLESTONE);

        Chest chest = (Chest) chestLoc.getBlock().getState();
        chest.setMetadata("spawned", new FixedMetadataValue(Index.getInstance(), "true"));
        InventoryManager.createChestInventory(p, chest, false, 0);
        chest.update(true);
    }

    private static Location getLocationInGround(Location loc) {
        Block down = loc.getBlock().getRelative(BlockFace.DOWN);
        if(down.isEmpty() || down.isLiquid() || down.getType() == Material.LONG_GRASS || down.getType() == Material.RED_ROSE
                || down.getType() == Material.LEAVES || down.getType() == Material.LEAVES_2)
            return getLocationInGround(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());

        return loc;
    }
}
