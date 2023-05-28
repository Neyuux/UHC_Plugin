package fr.neyuux.uhc.listeners;

import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.UHCWorld;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldListener implements Listener {

    private final UHC main;
    public WorldListener(UHC main) {
        this.main = main;
    }

    public static List<Chunk> keepChunk = new ArrayList<>();
    public static List<Block> blocksTowers = new ArrayList<>();


    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (keepChunk.contains(e.getChunk()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block b = e.getBlock();
        if(player == null) return;

        // Nether: QUARTZ NERF
        if(b.getType() == Material.QUARTZ_ORE && (boolean)GameConfig.ConfigurableParams.QUARTZ_XP_NERF.getValue())
            e.setExpToDrop(e.getExpToDrop() / 2);

        PlayerUHC up = main.getPlayerUHC(player.getUniqueId());
        if (b.getType() == Material.DIAMOND_ORE) up.addDiamonds(1);
        else if (b.getType() == Material.GOLD_ORE) up.addGolds(1);
        else if (b.getType() == Material.IRON_ORE) up.addIrons(1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(!(boolean)GameConfig.ConfigurableParams.TOWER.getValue() && !Scenarios.SKY_HIGH.isActivated()){
            // Anti tower
            Player p = e.getPlayer();
            Location loc = e.getBlock().getLocation();

            if (loc == loc.getWorld().getHighestBlockAt(loc).getLocation() && loc.getBlockY() >= 70 && UHCWorld.isOnSurface(p.getLocation())) {
                int nb = 0;

                while ((blocksTowers.contains(loc.clone().subtract(0,1,0).getBlock()) || loc.clone().subtract(0,1,0).getBlock().getType() == Material.AIR) && loc.clone().subtract(0,1,0).getBlock().getLocation().getBlockY() > 64 && nb < 10) {
                    nb++;
                    loc = loc.subtract(0,1,0);
                }
                if (nb >= 10) {
                    e.setCancelled(true);
                    p.sendMessage(UHC.getPrefix() + "§cLes towers sont désactivées.");
                    UHC.playNegativeSound(p);
                } else
                    blocksTowers.add(e.getBlock());
            } else
                blocksTowers.add(e.getBlock());
        }

    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        EntityType type = e.getEntityType();
        if(entity == null) return;
        if(type == null) return;
        if(entity.getKiller() == null) return;
        if(entity.getKiller().getType() != EntityType.PLAYER) return;

        Player killer = e.getEntity().getKiller();
        PlayerUHC up = main.getPlayerUHC(killer.getUniqueId());

        if(type == EntityType.CHICKEN || type == EntityType.COW || type == EntityType.HORSE || type == EntityType.SHEEP || type == EntityType.RABBIT
                || type == EntityType.MUSHROOM_COW || type == EntityType.PIG || type == EntityType.WOLF || type == EntityType.OCELOT || type == EntityType.SQUID)
            up.addAnimal();

        if(type == EntityType.BLAZE || type == EntityType.CAVE_SPIDER || type == EntityType.CREEPER || type == EntityType.ENDERMAN || type == EntityType.GHAST
                || type == EntityType.MAGMA_CUBE || type == EntityType.PIG_ZOMBIE || type == EntityType.SILVERFISH || type == EntityType.SKELETON
                || type == EntityType.SLIME || type == EntityType.SPIDER || type == EntityType.WITCH || type == EntityType.ZOMBIE)
            up.addMonster();
    }


}
