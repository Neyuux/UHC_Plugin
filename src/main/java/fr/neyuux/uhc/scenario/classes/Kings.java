package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kings extends Scenario implements Listener {
    public Kings() {
        super(Scenarios.KINGS, new ItemStack(Material.GOLD_HELMET));
    }

    private final List<PlayerUHC> kings = new ArrayList<>();


    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        Random random = new Random();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            for (UHCTeam team : UHC.getInstance().getUHCTeamManager().getTeams()) {
                if (team.getAlivePlayers().size() > 0) {
                    List<Player> players = new ArrayList<>();
                    for (PlayerUHC u : team.getAlivePlayers()) {
                        Player p = u.getPlayer().getPlayer();
                        if (p != null)
                            players.add(p);
                    }
                    Player king = players.get(random.nextInt(players.size()));
                    PlayerUHC ku = UHC.getInstance().getPlayerUHC(king.getUniqueId());
                    king.setDisplayName(team.getPrefix().toString() + "§l" + king.getName());
                    king.setPlayerListName(king.getDisplayName());
                    king.sendMessage(getPrefix() + "§6Vous avez été désigné comme Roi de l'équipe " + team.getTeam().getDisplayName() + " §6. Vous obtenez donc les effets : force, rapidité, résistance, résistance au feu, hâte et double vie. Si vous mourrez, le reste de votre équipe aura un effet de poison puissant pendant quelques secondes.");
                    team.sendMessage(getPrefix() + team.getPrefix().color.getColor() + "Le roi de votre équipe est " + king.getDisplayName() + ".");

                    king.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, true));
                    king.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
                    king.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, true));
                    king.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, true, true));
                    king.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, true));
                    ku.maxHealth *= 2.0;
                    king.setMaxHealth(ku.maxHealth);
                    ku.heal();

                    kings.add(ku);
                }
            }
        }, 101L);
    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent ev) {
        if (kings.contains(UHC.getInstance().getPlayerUHC(ev.getPlayer().getUniqueId()))) {
            ev.getPlayer().setDisplayName(UHC.getInstance().getPlayerUHC(ev.getPlayer().getUniqueId()).getTeam().getPrefix().toString() + "§l" + ev.getPlayer().getName());
            ev.getPlayer().setPlayerListName(ev.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onDeath(PlayerEliminationEvent e) {
        PlayerUHC ku = e.getPlayerUHC();
        if(kings.contains(ku)) {
            UHCTeam team = ku.getTeam();
            for (PlayerUHC u : team.getAlivePlayers()) {
                Player p = u.getPlayer().getPlayer();
                if (p != null) {
                    p.sendMessage(getPrefix() + "§cVotre roi est Mort. Vous obtenez un effet de poison pendant 30 secondes.");
                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 3, true, true));
                }
            }
        }
    }
}
