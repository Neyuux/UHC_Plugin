package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.GameEndEvent;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.listeners.FightListener;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.tasks.UHCRunnable;
import fr.neyuux.uhc.teams.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.neyuux.uhc.GameConfig.ConfigurableParams.SLOTS;
import static fr.neyuux.uhc.GameConfig.ConfigurableParams.TEAMTYPE;

public class TrueLove extends Scenario implements Listener {
    public TrueLove() {
        super(Scenarios.TRUE_LOVE, new ItemStack(Material.STICK));
    }

    public static int teamSize = 1;

    public static List<PlayerUHC> lovers = new ArrayList<>();
    private static final HashMap<PlayerUHC, UHCTeam> waitList = new HashMap<>();

    @Override
    protected void activate() {
        GameConfig.ConfigurableParams.TEAMTYPE.setValue(GameConfig.getTeamTypeString(1, false));
        int teamSize = GameConfig.getTeamTypeInt(TEAMTYPE.getValue().toString());
        if (((String)TEAMTYPE.getValue()).startsWith("To") || (Scenarios.TRUE_LOVE.isActivated() && teamSize > 1))
            for (PlayerUHC pl : UHC.getInstance().players)
                pl.getPlayer().getPlayer().getInventory().setItem(4, GameConfig.getChooseTeamBanner(pl));
        else {
            for (PlayerUHC pl : UHC.getInstance().players)
                pl.getPlayer().getPlayer().getInventory().remove(Material.BANNER);
        }

        UHC.getInstance().getUHCTeamManager().clearTeams();
        int p = (int) SLOTS.getValue();
        if (teamSize > 1) {
            int nt = BigDecimal.valueOf((double) p / teamSize).setScale(0, RoundingMode.UP).toBigInteger().intValue();
            if (nt == 0) nt = 1;
            while (nt != 0) {
                if (nt < 0) throw new IllegalArgumentException("nt est inferieur a 0");
                UHC.getInstance().getUHCTeamManager().createTeam();
                nt--;
            }
        }
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!UHC.getInstance().isState(Gstate.PLAYING)) {
                    cancel();
                    return;
                }
                if (UHCRunnable.timer < 3) return;
                for (PlayerUHC playerUHC : UHC.getInstance().getAlivePlayers())
                    if (!lovers.contains(playerUHC) && playerUHC.getPlayer().isOnline()) {
                        for (Entity ent : playerUHC.getPlayer().getPlayer().getNearbyEntities(40, 40, 40))
                            if (ent.getType().equals(EntityType.PLAYER)) {
                                PlayerUHC euhc = UHC.getInstance().getPlayerUHC(ent.getUniqueId());
                                if (euhc.isAlive() && !euhc.isSpec() && !lovers.contains(euhc)) {
                                    if (playerUHC.getTeam() != null && euhc.getTeam() != null && playerUHC.getTeam().hasPlayer(euhc)) continue;
                                    love(playerUHC, euhc);
                                }
                            }
                    }
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onElim(PlayerEliminationEvent ev) {
        if (ev.getPlayerUHC().getTeam() != null) {
            UHCTeam team = ev.getPlayerUHC().getTeam();
            List<PlayerUHC> players = new ArrayList<>(team.getAlivePlayers());
            if (players.size() == teamSize)
                for (PlayerUHC pu : players) lovers.remove(pu);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        PlayerUHC playerUHC = UHC.getInstance().getPlayerUHC(player.getUniqueId());

        if (playerUHC.isAlive() && !playerUHC.isSpec() && waitList.containsKey(playerUHC)) {
            waitList.get(playerUHC).add(player);
            UHC.sendTitle(playerUHC.getPlayer().getPlayer(), "§d§lTrue Love", waitList.get(playerUHC).getPrefix().color.getColor() + " Vous rejoignez l'équipe " + waitList.get(playerUHC).getTeam().getDisplayName() + " !", 23, 40, 7);
        }
    }

    @EventHandler
    public void onWin(GameEndEvent ev) {
        if (UHC.getInstance().getAlivePlayers().size() > teamSize * 2) ev.setCancelled(true);
    }


    public static void love(PlayerUHC player1UHC, PlayerUHC player2UHC) {
        if (player1UHC.getTeam() != null && player2UHC.getTeam() != null) {
            Bukkit.broadcastMessage(UHC.getPrefix() + Scenarios.TRUE_LOVE.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §dL'équipe " + player2UHC.getTeam().getTeam().getDisplayName() + " §det l'équipe " + player1UHC.getTeam().getTeam().getDisplayName() + " §dtombent éperdument amoureuses !");
            Bukkit.broadcastMessage(UHC.getPrefix() + "§6L'équipe " + player2UHC.getTeam().getTeam().getDisplayName() + " §6est éliminée...");
            for (PlayerUHC pu : player2UHC.getTeam().getPlayers()) {
                player2UHC.getTeam().leave(pu);
                if (pu.getPlayer().isOnline()) player1UHC.getTeam().add(pu.getPlayer().getPlayer());
                else waitList.put(pu, player1UHC.getTeam());
            }
            for (PlayerUHC pu : player1UHC.getTeam().getAlivePlayers()) {
                UHC.playPositiveSound(pu.getPlayer().getPlayer());
                UHC.sendTitle(pu.getPlayer().getPlayer(), "§d§lTrue Love", player1UHC.getTeam().getPrefix().color.getColor() + " Vous rejoignez l'équipe " + player1UHC.getTeam().getTeam().getDisplayName() + " !", 3, 40, 7);
                lovers.add(pu);
            }
        } else if (player1UHC.getTeam() == null && player2UHC.getTeam() == null) {
            UHCTeam team = UHC.getInstance().getUHCTeamManager().createTeam();
            team.add(player1UHC.getPlayer().getPlayer());
            lovers.add(player1UHC);
            team.add(player2UHC.getPlayer().getPlayer());
            lovers.add(player2UHC);
            UHC.playPositiveSound(player1UHC.getPlayer().getPlayer());
            UHC.sendTitle(player1UHC.getPlayer().getPlayer(), "§d§lTrue Love", team.getPrefix().color.getColor() + " Vous rejoignez l'équipe " + team.getTeam().getDisplayName() + " !", 3, 40, 7);
            UHC.playPositiveSound(player2UHC.getPlayer().getPlayer());
            UHC.sendTitle(player2UHC.getPlayer().getPlayer(), "§d§lTrue Love", team.getPrefix().color.getColor() + " Vous rejoignez l'équipe " + team.getTeam().getDisplayName() + " !", 3, 40, 7);
        } else {
            UHCTeam team = null;
            if (player1UHC.getTeam() != null) team = player1UHC.getTeam();
            if (player2UHC.getTeam() != null) team = player2UHC.getTeam();
            List<PlayerUHC> players = new ArrayList<>(team.getPlayers());
            players.removeAll(team.getDeathPlayers());
            if (players.size() < GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString())) {
                team.add(player1UHC.getPlayer().getPlayer());
                if (!lovers.contains(player1UHC)) lovers.add(player1UHC);
                team.add(player2UHC.getPlayer().getPlayer());
                if (!lovers.contains(player2UHC)) lovers.add(player2UHC);
                UHC.playPositiveSound(player1UHC.getPlayer().getPlayer());
                UHC.sendTitle(player1UHC.getPlayer().getPlayer(), "§d§lTrue Love", team.getPrefix().color.getColor() + " Vous rejoignez l'équipe " + team.getTeam().getDisplayName() + " !", 3, 40, 7);
                UHC.playPositiveSound(player2UHC.getPlayer().getPlayer());
                UHC.sendTitle(player2UHC.getPlayer().getPlayer(), "§d§lTrue Love", team.getPrefix().color.getColor() + " Vous rejoignez l'équipe " + team.getTeam().getDisplayName() + " !", 3, 40, 7);
            }
        }
        FightListener.checkWin();
    }
}
