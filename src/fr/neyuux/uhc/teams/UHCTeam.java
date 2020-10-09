package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UHCTeam {

    private Team team;
    private final TeamPrefix prefix;
    private final Set<PlayerUHC> players;
    private final Set<PlayerUHC> alivePlayers;
    private final Set<PlayerUHC> deathPlayers;
    private final Index main;
    public UHCTeam(Index main, TeamPrefix prefix) {
        this.main = main;
        this.prefix = prefix;
        this.players = new HashSet<>();
        this.alivePlayers = new HashSet<>();
        this.deathPlayers = new HashSet<>();

        setupTeam();
    }

    private void setupTeam() {
        Scoreboard scoreboard = main.getUHCTeamManager().getScoreboard();
        team = scoreboard.registerNewTeam(prefix.toString() + " " + prefix.color.getName());
        team.setPrefix(prefix.toString());
        team.setSuffix("§r");
        team.setAllowFriendlyFire(GameConfig.PlayerRules.FRIENDLYFIRE.getValue());
        team.setCanSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(NameTagVisibility.ALWAYS);
    }

    public void sendMessage(String msg) {
        for (PlayerUHC pu : players) {
            Player player = pu.getPlayer().getPlayer();
            if (player != null)
                player.sendMessage(msg);
        }
    }

    public void add(Player player) {
        if (main.getGameConfig().getTeamType().startsWith("To") && players.size() >= Integer.parseInt(main.getGameConfig().getTeamType().substring(2))) {
            player.sendMessage(main.getPrefix() + "§cL'équipe " + team.getDisplayName() + " §cest pleine !");
            return;
        }
        team.addEntry(player.getName());

        if (main.getGameConfig().getTeamType().startsWith("To")) {
            player.sendMessage(main.getPrefix() + prefix.color.getColor() + "Vous avez rejoint l'équipe " + team.getName() + " !");
            sendMessage(main.getPrefix() + player.getDisplayName() + prefix.color.getColor() + " a rejoint votre équipe !");
        }
        players.add(main.getPlayerUHC(player));
        alivePlayers.add(main.getPlayerUHC(player));

        player.setDisplayName(prefix.toString() + player.getName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void reconnect(Player player) {
        alivePlayers.add(main.getPlayerUHC(player));
        player.setDisplayName(prefix.toString() + player.getName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void leave(PlayerUHC pu) {
        if(!Scenarios.SWITCH.isActivated()) {
            if (alivePlayers.size() == 1 && alivePlayers.contains(pu))
                main.getUHCTeamManager().removeTeam(this);
        }
        Player player = pu.getPlayer().getPlayer();
        if (main.getGameConfig().getTeamType().startsWith("To") && player != null) {
            player.sendMessage(main.getPrefix() + prefix.color.getColor() + "Vous avez bien quitté l'équipe " + team.getName() + " !");
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getDisplayName());
        }
        team.removeEntry(main.getPlayerUHC(player).getPlayer().getName());
        players.remove(main.getPlayerUHC(player));
        alivePlayers.remove(main.getPlayerUHC(player));
    }

    public void leaveInGame(PlayerUHC pu) {
        alivePlayers.remove(pu);
    }

    public boolean hasPlayer(PlayerUHC pu) {
        return players.contains(pu);
    }

    public boolean isAlive(PlayerUHC playerUHC) {
        return alivePlayers.contains(playerUHC);
    }

    public void death(PlayerUHC pu) {
        deathPlayers.add(pu);
        alivePlayers.remove(pu);
    }

    public void revive(PlayerUHC pu) {
        alivePlayers.add(pu);
        deathPlayers.remove(pu);
        pu.getPlayer().getPlayer().setDisplayName(prefix.toString() + pu.getPlayer().getName());
        pu.getPlayer().getPlayer().setPlayerListName(pu.getPlayer().getPlayer().getDisplayName());
    }

    public void removeTeam() {
        for (PlayerUHC pu : players) {
            Player player = pu.getPlayer().getPlayer();
            if (player != null)
                player.setDisplayName(player.getName());
        }

        if (team != null)
            team.unregister();
    }

    public Team getTeam() {
        return team;
    }

    public int getKills() {
        int i = 0;
        for (PlayerUHC up : players)
            i += up.getKills();
        return i;
    }

    public int getAlivePlayersKills() {
        int i = 0;
        for (PlayerUHC up : alivePlayers)
            i += up.getKills();
        return i;
    }

    public TeamPrefix getPrefix() {
        return prefix;
    }

    public Set<PlayerUHC> getPlayers() {
        return players;
    }

    public Set<PlayerUHC> getAlivePlayers() {
        return alivePlayers;
    }

    public ArrayList<PlayerUHC> getListPlayers(){
        return new ArrayList<>(alivePlayers);
    }

}
