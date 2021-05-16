package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class OreLimiter extends Scenario implements Listener {
    public OreLimiter() {
        super(Scenarios.ORE_LIMITER, new ItemStack(Material.IRON_ORE));
    }

    public static int diamonds = 20, golds = 60, irons = 120;

    @Override
    protected void activate() {
        
    }


    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }
    
    
    @EventHandler
    public void onMineOre(BlockBreakEvent e) {
        Player player = e.getPlayer();
        PlayerUHC up = UHC.getInstance().getPlayerUHC(player);
        Block b = e.getBlock();
        if (b.getType() == Material.DIAMOND_ORE)
            if (up.getDiamonds() > diamonds) {
                int o = new Random().nextInt(2) + 1;
                e.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItem(b.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.GOLD_INGOT, o));
                ExperienceOrb orb = b.getWorld().spawn(b.getLocation().add(0.5, 0.5, 0.5), ExperienceOrb.class);
                orb.setExperience(e.getExpToDrop());
                player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §bVous avez dépassé la limite de dimants. Vous avez reçu §6" + o + " d'or§b à place.");
            } else if (up.getDiamonds() == diamonds)
                player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§b La limite de diamants a été atteinte. Désormais lorsque vous minerez du diamant, vous receverez 1 ou 2 ors.");
        if (b.getType().equals(Material.GOLD_ORE))
                if (up.getGolds() > golds) {
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                    player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §eVous avez dépassé la limite d'ors.");
                } else if (up.getGolds() == golds)
                    player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§e La limite de d'ors a été atteinte.");
        if (b.getType().equals(Material.IRON_ORE))
                if (up.getIrons() > irons) {
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                    player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §fVous avez dépassé la limite de fers.");
                } else if (up.getIrons() == irons)
                    player.sendMessage(UHC.getInstance().getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§f La limite de fers a été atteinte.");

    }
}
