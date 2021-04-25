package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.events.TeamChangeEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TeamHealth extends Scenario implements Listener {
    public TeamHealth() {
        super(Scenarios.TEAM_HEALTH, new ItemStack(Material.FIREWORK));
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
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    @EventHandler
    public void onChangeTeam(TeamChangeEvent ev) {
        updateHealth(ev.getTeam(), 0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHealth(EntityRegainHealthEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER)) {
            Player player = (Player)ev.getEntity();
            PlayerUHC playerUHC = Index.getInstance().getPlayerUHC(player);
            if (playerUHC.isAlive() && playerUHC.getTeam() != null)
                updateHealth(playerUHC.getTeam(), ev.getAmount());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent ev) {
        if (ev.getEntityType().equals(EntityType.PLAYER)) {
            Player player = (Player)ev.getEntity();
            PlayerUHC playerUHC = Index.getInstance().getPlayerUHC(player);
            if (playerUHC.isAlive() && playerUHC.getTeam() != null)
                updateHealth(playerUHC.getTeam(), -ev.getFinalDamage());
        }
    }


    public static void updateHealth(UHCTeam team, double added) {
        int a = BigDecimal.valueOf(added).setScale(0, RoundingMode.HALF_UP).toBigInteger().intValue();
        for (PlayerUHC pu : team.getAlivePlayers()) {
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").getScore(pu.getPlayer().getName()).setScore((int)pu.getTeam().getHealth() + a);
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("healthBelow").getScore(pu.getPlayer().getName()).setScore((int)pu.getTeam().getHealth() + a);
        }
    }
}
