package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class UHCTeamManager {

    private final UHC main;
    private List<UHCTeam> teams;
    public static int baseteams;
    public static List<PlayerUHC> baseplayers;

    public UHCTeamManager(UHC main) {
        this.main = main;
        teams = new ArrayList<>();
        UHCTeamColors.used = 0;
        UHCTeamColors.taupeused = 0;
        baseplayers = null;
        baseteams = 0;
    }

    public UHCTeam createTeam() {
        UHCTeam UHCTeam = new UHCTeam(main, new TeamPrefix(main, null, null));
        teams.add(UHCTeam);
        teams = sortTeams();
        return UHCTeam;
    }

    public UHCTeam createTeam(TeamPrefix prefix) {
        UHCTeam uhcTeam = new UHCTeam(main, prefix);
        teams.add(uhcTeam);
        teams = sortTeams();
        return  uhcTeam;
    }

    public void removeTeam(UHCTeam team) {
        team.removeTeam();
        teams.remove(team);
        UHCTeamColors.used =- 1;
    }

    public void clearTeams() {
        for (UHCTeam t : teams) {
            try {
                for (PlayerUHC p : t.getPlayers()) t.leave(p);
            } catch (ConcurrentModificationException ignored) {}
            t.removeTeam();
        }
        teams.clear();
        UHCTeamColors.used = 0;
    }

    public Scoreboard getScoreboard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public List<UHCTeam> getTeams() {
        return teams;
    }

    public List<UHCTeam> getAliveTeams() {
        List<UHCTeam> l = new ArrayList<>();
        for (UHCTeam t : getTeams())
            if (t.getAlivePlayers().size() != 0) l.add(t);
        return l;
    }

    public void randomTeams() {
        int max = Integer.parseInt(((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).substring("Random To".length()));
        List<PlayerUHC> players = new ArrayList<>(main.getAlivePlayers());

        clearTeams();
        int nbTeams = players.size() / max;
        if ((players.size() % max) != 0)
            nbTeams++;
        for (int i = 0; i <= nbTeams; i++)
            attributeToTeam(players, max);
    }

    private void attributeToTeam(List<PlayerUHC> players, int teamSize) {
        Collections.shuffle(players);
        UHCTeam team = createTeam();
        for (int i = 0; i < teamSize; i++) {
            if (players.isEmpty())
                break;
            PlayerUHC player = players.remove(0);
            team.add(player.getPlayer().getPlayer());
        }
    }


    public static ItemStack getTeamBanner(PlayerUHC player) {
        if (player.getTeam() == null) return new ItemsStack(Material.BANNER, (short)15).toItemStack();
        else return player.getTeam().getBanner();
    }

    public UHCTeam getTeamByDisplayName(String dname) {
        for (UHCTeam t : teams)
            if (t.getTeam().getDisplayName().equals(dname))
                return t;
        return null;
    }

    private List<UHCTeam> sortTeams() {
        List<UHCTeam> l = new ArrayList<>(teams);
        l.sort((o1, o2) -> {
            Integer i1 = o1.id;
            Integer i2 = o2.id;
            return i1.compareTo(i2);
        });
        return l;
    }
}
