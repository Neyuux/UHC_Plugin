package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.InventoryManager;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class TimeBomb extends Scenario implements Listener {
    public TimeBomb() {
        super(Scenarios.TIME_BOMB, new ItemStack(Material.TRAPPED_CHEST));
    }

    public static int timer = 20;

    @Override
    public void activate() {
        if ((boolean) GameConfig.ConfigurableParams.BARRIER_HEAD.getValue()) Bukkit.broadcastMessage(UHC.getPrefix() + "§cVeuillez désactiver l'apparition d'un poteau au kill pour que " + this.scenario.getDisplayName() + " §cpuisse fonctionner.");
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
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
        setupTimeBomb(e.getPlayerUHC(), e.getStuffLocation());
    }


    public static void setupTimeBomb(PlayerUHC p, Location stuffLocation) {
        Location loc = stuffLocation.clone();
        Block b = loc.getBlock();
        Block b1 = loc.add(0,0,1).getBlock();
        b.setType(Material.CHEST);
        b1.setType(Material.CHEST);

        Chest chest = (Chest) loc.getBlock().getState();
        chest.setMetadata("spawned", new FixedMetadataValue(UHC.getInstance(), "true"));
        InventoryManager.createChestInventory(p, chest, true, timer);
        chest.update(true);
    }
}
