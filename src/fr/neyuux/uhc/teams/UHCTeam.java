package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Banner;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.DyeColor.*;
import static org.bukkit.block.banner.PatternType.*;

public class UHCTeam {

    private Team team;
    private final TeamPrefix prefix;
    private final Set<PlayerUHC> players;
    private final Set<PlayerUHC> alivePlayers;
    private final Set<PlayerUHC> deathPlayers;
    private final Index main;
    public final int id;
    public UHCTeam(Index main, TeamPrefix prefix) {
        this.main = main;
        this.prefix = prefix;
        this.players = new HashSet<>();
        this.alivePlayers = new HashSet<>();
        this.deathPlayers = new HashSet<>();
        this.id = main.getUHCTeamManager().getTeams().size() + 1;

        setupTeam();
    }

    private void setupTeam() {
        Scoreboard scoreboard = main.getUHCTeamManager().getScoreboard();
        team = scoreboard.registerNewTeam(prefix.toString() + prefix.color.getName());
        team.setDisplayName(prefix.toString() + prefix.color.getDisplayName());
        team.setPrefix(prefix.toString());
        team.setSuffix("§r");
        team.setAllowFriendlyFire(((boolean)GameConfig.ConfigurableParams.FRIENDLY_FIRE.getValue()));
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
        if (((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To") && players.size() >= Integer.parseInt(((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).substring(2))) {
            player.sendMessage(main.getPrefix() + "§cL'équipe " + team.getDisplayName() + " §cest pleine !");
            return;
        }
        team.addEntry(player.getName());

        if (((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To")) {
            player.sendMessage(main.getPrefix() + prefix.color.getColor() + "Vous avez rejoint l'équipe " + team.getDisplayName() + " !");
            sendMessage(main.getPrefix() + player.getDisplayName() + prefix.color.getColor() + " a rejoint votre équipe !");
        }
        players.add(main.getPlayerUHC(player));
        alivePlayers.add(main.getPlayerUHC(player));
        main.getPlayerUHC(player).setTeam(this);

        player.setDisplayName(prefix.toString() + player.getName());
        if (main.getPlayerUHC(player).isHost())
            player.setDisplayName(TeamPrefix.getHostPrefix() + player.getDisplayName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void reconnect(Player player) {
        alivePlayers.add(main.getPlayerUHC(player));
        player.setDisplayName(prefix.toString() + player.getName());
        player.setPlayerListName(player.getDisplayName());
    }

    public void leave(PlayerUHC pu) {
        /*if(main.isState(Gstate.PLAYING) && !Scenarios.SWITCH.isActivated())
            if (alivePlayers.size() == 1 && alivePlayers.contains(pu))
                main.getUHCTeamManager().removeTeam(this);*/
        if (pu.getPlayer().isOnline()) {
            Player player = pu.getPlayer().getPlayer();
            if (((String) GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To")) {
                player.sendMessage(main.getPrefix() + prefix.color.getColor() + "Vous avez bien quitté l'équipe " + team.getDisplayName() + " !");
                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getDisplayName());
            }
            team.removeEntry(main.getPlayerUHC(player).getPlayer().getName());
            if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) {
                if (pu.isHost())
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(pu.getPlayer().getName());
                else
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(pu.getPlayer().getName());
                player.setDisplayName(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix() + player.getName());
                player.setPlayerListName(player.getDisplayName());
            }
            players.remove(main.getPlayerUHC(player));
            alivePlayers.remove(main.getPlayerUHC(player));
        } else {
            OfflinePlayer player = pu.getPlayer();
            team.removeEntry(main.getPlayerUHC(player).getPlayer().getName());
            if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) if (pu.isHost())
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(player.getName());
            else
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(player.getName());
            players.remove(main.getPlayerUHC(player));
            alivePlayers.remove(main.getPlayerUHC(player));
        }
        pu.setTeam(null);
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

    public double getHealth() {
        int health = 0;
        for (PlayerUHC pu : alivePlayers)
            health += pu.health + pu.absorption;
        return health;
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

    public ItemStack getBanner() {
        Banner b = new Banner(Material.BANNER);
        ItemStack it = b.toItemStack(1);
        BannerMeta bm = (BannerMeta)it.getItemMeta();
        bm.setBaseColor(getPrefix().color.getDyecolor());
        if (bm.getBaseColor().equals(RED) || bm.getBaseColor().equals(BLUE))
            if (getPrefix().color.getName().startsWith("Dark ")) {
                bm.setPatterns(Arrays.asList(new Pattern(BLACK, GRADIENT_UP), new Pattern(bm.getBaseColor(), GRADIENT),
                        new Pattern(bm.getBaseColor(), GRADIENT), new Pattern(BLACK, GRADIENT), new Pattern(bm.getBaseColor(), GRADIENT)));
            }

        DyeColor c = bm.getBaseColor();
        if (getPrefix().symbol.equals(Symbols.HEARTH + " ")) {
            bm.addPattern(new Pattern(BLACK, CIRCLE_MIDDLE));
            bm.addPattern(new Pattern(c, TRIANGLE_TOP));
        } else if (getPrefix().symbol.equals(Symbols.STARBALL + " ")) {
            bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
            bm.addPattern(new Pattern(BLACK, CREEPER));
            bm.addPattern(new Pattern(c, TRIANGLE_BOTTOM));
            bm.addPattern(new Pattern(BLACK, CIRCLE_MIDDLE));
        } else if (getPrefix().symbol.equals(Symbols.ARROW_RIGHT_FULL + " ")) {
            bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
            bm.addPattern(new Pattern(BLACK, STRIPE_LEFT));
            bm.addPattern(new Pattern(c, SQUARE_TOP_LEFT));
            bm.addPattern(new Pattern(c, SQUARE_BOTTOM_LEFT));
            bm.addPattern(new Pattern(c, BORDER));
        } else if (getPrefix().symbol.equals(Symbols.SNOWMAN + " ")) {
            int d = c.ordinal();
            bm.setBaseColor(BLACK);
            DyeColor bc = DyeColor.values()[d];
            bm.addPattern(new Pattern(bc, STRIPE_TOP));
            bm.addPattern(new Pattern(bc, CURLY_BORDER));
            bm.addPattern(new Pattern(bc, CURLY_BORDER));
            bm.addPattern(new Pattern(bc, CURLY_BORDER));
            bm.addPattern(new Pattern(bc, BORDER));
        } else if (getPrefix().symbol.equals(Symbols.CROSS + " ")) {
            bm.addPattern(new Pattern(BLACK, CROSS));
            bm.addPattern(new Pattern(c, BORDER));
            bm.addPattern(new Pattern(c, BORDER));
            bm.addPattern(new Pattern(c, CURLY_BORDER));
        } else if (getPrefix().symbol.equals(Symbols.OK + " ")) {
            bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
            bm.addPattern(new Pattern(c, HALF_HORIZONTAL));
            bm.addPattern(new Pattern(c, CIRCLE_MIDDLE));
            bm.addPattern(new Pattern(c, STRIPE_LEFT));
        } else if (getPrefix().symbol.equals(Symbols.NUCLEAR + " ")) {
            int d = c.ordinal();
            bm.setBaseColor(BLACK);
            DyeColor bc = DyeColor.values()[d];
            bm.addPattern(new Pattern(bc, HALF_HORIZONTAL_MIRROR));
            bm.addPattern(new Pattern(BLACK, TRIANGLE_BOTTOM));
            bm.addPattern(new Pattern(bc, TRIANGLE_TOP));
            bm.addPattern(new Pattern(bc, STRIPE_TOP));
            bm.addPattern(new Pattern(bc, STRIPE_BOTTOM));
            bm.addPattern(new Pattern(bc, CURLY_BORDER));
        } else if (getPrefix().symbol.equals(Symbols.INFINITE + " ")) {
            bm.addPattern(new Pattern(BLACK, STRIPE_BOTTOM));
            bm.addPattern(new Pattern(BLACK, STRIPE_TOP));
            bm.addPattern(new Pattern(c, RHOMBUS_MIDDLE));
            bm.addPattern(new Pattern(c, BORDER));
            bm.addPattern(new Pattern(BLACK, CROSS));
            bm.addPattern(new Pattern(c, CURLY_BORDER));
        } else if (getPrefix().symbol.equals(Symbols.CERCLED_S + " ")) {
            bm.addPattern(new Pattern(BLACK, STRIPE_BOTTOM));
            bm.addPattern(new Pattern(c, DIAGONAL_RIGHT_MIRROR));
            bm.addPattern(new Pattern(BLACK, STRIPE_TOP));
            bm.addPattern(new Pattern(c, RHOMBUS_MIDDLE));
            bm.addPattern(new Pattern(BLACK, STRIPE_DOWNRIGHT));
            bm.addPattern(new Pattern(c, BORDER));
            bm.addPattern(new Pattern(c, CURLY_BORDER));
        }
        bm.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        it.setItemMeta(bm);
        return it;
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

    public Set<PlayerUHC> getDeathPlayers() {
        return deathPlayers;
    }

    public ArrayList<PlayerUHC> getListPlayers(){
        return new ArrayList<>(alivePlayers);
    }

}
