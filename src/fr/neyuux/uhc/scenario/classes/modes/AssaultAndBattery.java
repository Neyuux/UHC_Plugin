package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.events.PlayerReviveEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssaultAndBattery extends Scenario implements Listener {
    public AssaultAndBattery() {
        super(Scenarios.ASSAULT_AND_BATTERY, new ItemStack(Material.BOW));
    }

    public static boolean hasRandomChoice = true;

    public static final List<PlayerUHC> assaults = new ArrayList<>();
    public static final List<PlayerUHC> batteries = new ArrayList<>();

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (hasRandomChoice) {
                for (UHCTeam t : UHC.getInstance().getUHCTeamManager().getAliveTeams()) {
                    List<PlayerUHC> ps = new ArrayList<>(t.getListPlayers());
                    PlayerUHC assault = ps.remove(new Random().nextInt(2));
                    if (ps.size() != 0) {
                        PlayerUHC battery = ps.remove(0);

                        assaults.add(assault);
                        batteries.add(battery);
                        if (assault.getPlayer().isOnline())
                            assault.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVous êtes §4§lAssault§c, vous ne pouvez donc vous battre qu'au corps-à-corps et toute utilisation d'arc, de canne à pêche, de boule de neige ou d'oeuf vous est impossible. Si votre coéquipier qui est Battery meurt, vous pourrez de nouveau utiliser l'arc et les autres items cités précédemment.");
                        if (battery.getPlayer().isOnline())
                            battery.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVous êtes §a§lBattery§c, vous ne pouvez donc vous battre qu'à l'arc ou en utilisant des projectiles et toute utilisation d'épée, d'hache ou d'armes de corps-à-corps vous est impossible. Vous pouvez également utiliser la canne à pêche, les boules de neige et les oeufs en combat. Si votre coéquipier qui est Assault meurt, vous pourrez de nouveau utiliser les armes de corps-à-corps.");
                    } else {
                        assaults.add(assault);
                        batteries.add(assault);

                        if (assault.getPlayer().isOnline())
                            assault.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVous êtes seul. Par conséquent vous obtenez deux rôles §4§lAssault§c&§a§lBattery§c. Vous pouvez utiliser ce que vous voulez pour vous battre, et ce, jusqu'à la fin de la partie.");
                    }
                }
            } else
                Bukkit.broadcastMessage(getPrefix() + "§cPour définir si vous êtes Assault ou Battery, il vous faudra utiliser l'arme choisie sur un joueur de la partie (si vous frapper quelqu'un avec une arme de corps-à-corps, vous serez Assault et vice-versa.");
        }, 102L);
    }

    @Override
    public boolean checkStart() {
        return GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString().endsWith("To2");
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent ev) {
        if (ev.getDamager().getType().equals(EntityType.PLAYER) && ev.getEntityType().equals(EntityType.PLAYER)) {
            Player d = (Player)ev.getDamager();
            PlayerUHC du = UHC.getInstance().getPlayerUHC(d);
            if (batteries.contains(du) && !assaults.contains(du)) {
                ev.setCancelled(true);
                UHC.sendActionBar(d, getPrefix() + "§cVous ne pouvez pas frapper au corps-à-corps !");
                UHC.playNegativeSound(d);
            } else if (!batteries.contains(du) && !assaults.contains(du) && !hasRandomChoice) {
                List<PlayerUHC> ps = new ArrayList<>(du.getTeam().getListPlayers());
                ps.remove(du);
                PlayerUHC battery = ps.remove(0);
                assaults.add(du);
                batteries.add(battery);
                d.sendMessage(getPrefix() + "§cVous êtes §4§lAssault§c, vous ne pouvez donc vous battre qu'au corps-à-corps et toute utilisation d'arc, de canne à pêche, de boule de neige ou d'oeuf vous est impossible. Si votre coéquipier qui est Battery meurt, vous pourrez de nouveau utiliser l'arc et les autres items cités précédemment.");
                if (battery.getPlayer().isOnline())
                    battery.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVous êtes §a§lBattery§c, vous ne pouvez donc vous battre qu'à l'arc ou en utilisant des projectiles et toute utilisation d'épée, d'hache ou d'armes de corps-à-corps vous est impossible. Vous pouvez également utiliser la canne à pêche, les boules de neige et les oeufs en combat. Si votre coéquipier qui est Assault meurt, vous pourrez de nouveau utiliser les armes de corps-à-corps.");
            }
        } else if (ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player && ev.getEntityType().equals(EntityType.PLAYER)) {
            Player d = (Player)((Projectile)ev.getDamager()).getShooter();
            PlayerUHC du = UHC.getInstance().getPlayerUHC(d);
            if (assaults.contains(du) && !batteries.contains(du)) {
                ev.setCancelled(true);
                UHC.sendActionBar(d, getPrefix() + "§cVous ne pouvez pas utiliser de projectiles !");
                UHC.playNegativeSound(d);
            } else if (!batteries.contains(du) && !assaults.contains(du) && !hasRandomChoice) {
                List<PlayerUHC> ps = new ArrayList<>(du.getTeam().getListPlayers());
                ps.remove(du);
                PlayerUHC assault = ps.remove(0);
                batteries.add(du);
                assaults.add(assault);
                d.sendMessage(getPrefix() + "§cVous êtes §a§lBattery§c, vous ne pouvez donc vous battre qu'à l'arc ou en utilisant des projectiles et toute utilisation d'épée, d'hache ou d'armes de corps-à-corps vous est impossible. Vous pouvez également utiliser la canne à pêche, les boules de neige et les oeufs en combat. Si votre coéquipier qui est Assault meurt, vous pourrez de nouveau utiliser les armes de corps-à-corps.");
                if (assault.getPlayer().isOnline())
                    assault.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVous êtes §4§lAssault§c, vous ne pouvez donc vous battre qu'au corps-à-corps et toute utilisation d'arc, de canne à pêche, de boule de neige ou d'oeuf vous est impossible. Si votre coéquipier qui est Battery meurt, vous pourrez de nouveau utiliser l'arc et les autres items cités précédemment.");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerEliminationEvent ev) {
        List<PlayerUHC> ps = new ArrayList<>(ev.getPlayerUHC().getTeam().getListPlayers());
        ps.remove(ev.getPlayerUHC());
        if (ps.size() != 0) {
            PlayerUHC te = ps.remove(0);
            if (!assaults.contains(te)) assaults.add(te);
            if (!batteries.contains(te)) batteries.add(te);
            if (te.getPlayer().isOnline())
                te.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVotre coéquipier est mort... Désormais, vous devenez Assault&Battery vous pouvez donc utiliser tous les objets pour vous battre.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRevive(PlayerReviveEvent ev) {
        if (!ev.isCancelled()) {
            List<PlayerUHC> ps = new ArrayList<>(ev.getPlayerUHC().getTeam().getListPlayers());
            ps.remove(ev.getPlayerUHC());
            PlayerUHC te = ps.remove(0);
            if (assaults.contains(ev.getPlayerUHC())) {
                assaults.remove(te);
                if (te.getPlayer().isOnline()) te.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVotre coéquipier a été ressucité ! Vous redevenez donc §a§lBattery§c !");
            } else if (batteries.contains(ev.getPlayerUHC())) {
                batteries.remove(te);
                if (te.getPlayer().isOnline()) te.getPlayer().getPlayer().sendMessage(getPrefix() + "§cVotre coéquipier a été ressucité ! Vous redevenez donc §4§lAssault§c !");
            }
        }
    }


    public static boolean hasRole(PlayerUHC pu) {
        return assaults.contains(pu) || batteries.contains(pu);
    }

}
