package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class UHCTeamManager {

    private Index main;
    private Scoreboard scoreboard;
    private Set<UHCTeam> teams;

    public UHCTeamManager(Index main) {
        this.main = main;
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        teams = new HashSet<>();
    }

    public UHCTeam createTeam() {
        UHCTeam UHCTeam = new UHCTeam(main, new TeamPrefix(main, null, null));
        teams.add(UHCTeam);
        return UHCTeam;
    }

    public UHCTeam createTeam(TeamPrefix prefix) {
        UHCTeam uhcTeam = new UHCTeam(main, prefix);
        teams.add(uhcTeam);
        return  uhcTeam;
    }

    public void removeTeam(UHCTeam team) {
        team.removeTeam();
        teams.remove(team);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Set<UHCTeam> getTeams() {
        return teams;
    }

    public void randomTeams() {
        int max = Teamsize.getCurrentTeam().getNumberPlayer();
        List<PlayerUHC> players = new ArrayList<>();
        players.addAll(main.players);

        int nbTeams = players.size() / max;
        if ((players.size() % max) != 0)
            nbTeams++;
        for (int i = 0; i <= nbTeams; i++)
            attributeToTeam(players, max);
    }

    public void randomTeamsRedVSBlue() {
        List<PlayerUHC> players = new ArrayList<>();
        players.addAll(main.players);

        int max = players.size() / 2;
        boolean two = false;
        for (int i = 0; i <= 1; i++) {
            if (i == 1) {
                two = true;
                if ((players.size() % 2) != 0)
                    max++;
            }
            attributeToTeamRedVSBlue(players, max, two);
        }
    }

    private void attributeToTeam(List<PlayerUHC> players, int teamSize) {
        Collections.shuffle(players);
        UHCTeam team = createTeam();
        for (int i = 0; i < teamSize; i++) {
            if (players.isEmpty())
                break;
            PlayerUHC player = players.get(0);
            team.add(player.getPlayer().getPlayer());
            players.remove(player);
        }
    }

    private void attributeToTeamRedVSBlue(List<PlayerUHC> players, int teamSize, boolean two) {
        Collections.shuffle(players);
        TeamPrefix prefix = two ? new TeamPrefix(main, UHCTeamColors.RED, "") : new TeamPrefix(main, UHCTeamColors.DARK_AQUA, "");
        UHCTeam team = createTeam(prefix);
        for (int i = 0; i <= teamSize; i++) {
            if (players.isEmpty())
                break;
            PlayerUHC player = players.get(0);
            team.add(player.getPlayer().getPlayer());
            players.remove(player);
        }
    }
}
