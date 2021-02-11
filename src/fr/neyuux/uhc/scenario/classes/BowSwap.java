package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BowSwap extends Scenario implements Listener {
    public BowSwap() {
        super(Scenarios.BOW_SWAP, new ItemStack(Material.BOW));
    }

    public static int percentage = 30;

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent ev) {
        if (ev.getDamager().getType().equals(EntityType.ARROW) && ((Arrow)ev.getDamager()).getShooter() instanceof Player && ev.getEntityType().equals(EntityType.PLAYER)) {
            Player p1 = (Player) ev.getEntity();
            Player p2 = (Player) ((Arrow)ev.getDamager()).getShooter();
            PlayerUHC pu1 = Index.getInstance().getPlayerUHC(p1);
            PlayerUHC pu2 = Index.getInstance().getPlayerUHC(p2);
            int r = new Random().nextInt(100) + 1;
            if (pu1.isAlive() && pu2.isAlive() && r <= percentage) {
                Location l1 = p1.getLocation().clone();
                Location l2 = p2.getLocation().clone();
                p1.teleport(l2);
                p2.teleport(l1);

                p1.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " �6Vous avez swapp� avec " + p2.getDisplayName() +"�6.");
                p2.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " �6Vous avez swapp� avec " + p1.getDisplayName() +"�6.");
            }
        }
    }
}
