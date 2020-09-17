package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UHCTeam {

    private Team team;
    private TeamPrefix prefix;
    private Set<PlayerUHC> players;
    private Set<PlayerUHC> alivePlayers;
    private Set<PlayerUHC> deathPlayers;
    private Set<PlayerUHC> pendingInvitations;
    private Index main;
    public UHCTeam(Index main, TeamPrefix prefix) {
        this.main = main;
        this.prefix = prefix;
        this.players = new HashSet<>();
        this.alivePlayers = new HashSet<>();
        this.deathPlayers = new HashSet<>();
        this.pendingInvitations = new HashSet<>();

        setupTeam();
    }

    private void setupTeam() {
        Scoreboard scoreboard = main.getUHCTeamManager().getScoreboard();
        team = scoreboard.registerNewTeam(prefix.toString() + " " + prefix.color.getName());
        team.setPrefix(prefix.toString());
        team.setSuffix("§r");
        //team.setAllowFriendlyFire(Joueur.FRIENDLYFIRE.getValue());
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
        Teamsize teams = Teamsize.getCurrentTeam();
        if (teams.getName().startsWith("To")  && players.size() >= teams.getNumberPlayer()) {
            ChatUtil.sendMessage(player, Messages.TEAM_FULL);
            return;
        }
        team.addEntry(player.getName());

        players.add(main.getPlayerUHC(player));
        alivePlayers.add(main.getPlayerUHC(player));
        if (teams.getName().startsWith("To") && !isOwner(player.getUniqueId()))
            sendMessage(Messages.PLAYER_JOIN_TEAM, player.getName());

        player.setDisplayName(prefix.toString() + player.getName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void reconnect(Player player) {
        alivePlayers.add(main.getPlayerUHC(player));
        player.setDisplayName(prefix.toString() + player.getName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void leave(PlayerUHC pu) {
        Teamsize teams = Teamsize.getCurrentTeam();
        if(!Scenarios.SWITCH.getActivated()) {
            if (isOwner(uuid) && teams.getName().startsWith("To") && UHCPlayerManager.getPlayer(uuid).isPlaying()) {
                sendMessage(Messages.DELETE_TEAM);
                main.getUHCTeamManager().removeTeam(this);
                return;
            }
        }
        Player player = pu.getPlayer().getPlayer();
        if (teams.getName().startsWith("To") && player != null) {
            sendMessage(Messages.PLAYER_LEAVE_TEAM, player.getName());
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
        pu.getPlayer().getPlayer().setDisplayName(prefix.color + prefix.symbol + pu.getPlayer().getName());
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

    public int getKillsOnlyInLive() {
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
        return new ArrayList<PlayerUHC>(alivePlayers);
    }

}
