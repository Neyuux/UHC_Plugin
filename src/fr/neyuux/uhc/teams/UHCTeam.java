package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.events.TeamChangeEvent;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
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

import java.util.*;

import static org.bukkit.DyeColor.*;
import static org.bukkit.block.banner.PatternType.*;

public class UHCTeam {

    private Team team;
    private final TeamPrefix prefix;
    private final Set<PlayerUHC> players;
    private final Set<PlayerUHC> alivePlayers;
    private final Set<PlayerUHC> deathPlayers;
    private final UHC main;
    public final int id;
    public UHCTeam(UHC main, TeamPrefix prefix) {
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
        if (!prefix.isTaupePrefix()) {
            team = scoreboard.registerNewTeam(prefix + prefix.color.getName());
            team.setDisplayName(prefix + prefix.color.getDisplayName());
        } else {
            if (!prefix.isSuperTaupePrefix()) {
                team = scoreboard.registerNewTeam("AB" + prefix.color.getColor() + "Taupe " + prefix.symbol.charAt(8));
                team.setDisplayName(prefix.color.getColor() + "Taupe " + prefix.symbol.charAt(8));
            } else {
                if (!Moles.areSuperMolesTogether) {
                    team = scoreboard.registerNewTeam("AASuper Taupe " + prefix.symbol.charAt(12));
                    team.setDisplayName(prefix.color.getColor() + "Super Taupe " + prefix.symbol.charAt(12));
                } else {
                    team = scoreboard.registerNewTeam("AAßlSuper Taupes");
                    team.setDisplayName("ßfßlSuper Taupes");
                }
            }
        }
        team.setPrefix(prefix.toString());
        team.setSuffix("ßr");
        team.setAllowFriendlyFire(((boolean)GameConfig.ConfigurableParams.FRIENDLY_FIRE.getValue()));
        team.setCanSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(NameTagVisibility.ALWAYS);
    }

    public void sendMessage(String msg) {
        for (PlayerUHC pu : players) {
            if (pu.getPlayer().isOnline())
                pu.getPlayer().getPlayer().sendMessage(msg);
        }
    }

    public void add(Player player) {
        if (((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To") && players.size() >= GameConfig.getTeamTypeInt(GameConfig.ConfigurableParams.TEAMTYPE.getValue().toString()) && !prefix.isTaupePrefix() && !(main.isState(Gstate.PLAYING) && (Scenarios.SWITCH.isActivated() || Scenarios.TRUE_LOVE.isActivated()))) {
            player.sendMessage(UHC.getPrefix() + "ßcL'Èquipe " + team.getDisplayName() + " ßcest pleine !");
            return;
        }
        if (hasPlayer(main.getPlayerUHC(player))) return;
        team.addEntry(player.getName());

        if (((String)GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To")) {
            player.sendMessage(UHC.getPrefix() + prefix.color.getColor() + "Vous avez rejoint l'Èquipe " + team.getDisplayName() + " !");
            sendMessage(UHC.getPrefix() + player.getDisplayName() + prefix.color.getColor() + " a rejoint votre Èquipe !");
        }
        players.add(main.getPlayerUHC(player));
        alivePlayers.add(main.getPlayerUHC(player));
        main.getPlayerUHC(player).setTeam(this);

        player.setDisplayName(prefix.toString() + player.getName());
        if (main.getPlayerUHC(player).isHost() && !main.isState(Gstate.PLAYING))
            player.setDisplayName(TeamPrefix.getHostPrefix() + player.getDisplayName());
        player.setPlayerListName(player.getDisplayName());
        if (!Scenarios.SLAVE_MARKET.isActivated() || main.isState(Gstate.PLAYING))main.boards.get(main.getPlayerUHC(player)).setLine(2, "ßeßl…quipe ße: " + team.getDisplayName());
        main.boards.get(main.getPlayerUHC(player)).setLine(0, player.getDisplayName());

        Bukkit.getPluginManager().callEvent(new TeamChangeEvent(main.getPlayerUHC(player), this));
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
                player.sendMessage(UHC.getPrefix() + prefix.color.getColor() + "Vous avez bien quittÈ l'Èquipe " + team.getDisplayName() + " !");
                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getDisplayName());

                if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) {
                    BannerMeta bm = (BannerMeta) player.getInventory().getItem(4).getItemMeta();
                    bm.setPatterns(Collections.emptyList());
                    bm.setBaseColor(DyeColor.WHITE);
                    player.getInventory().getItem(4).setItemMeta(bm);
                }

            }
            team.removeEntry(pu.getPlayer().getName());
            if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) {
                if (pu.isHost())
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(pu.getPlayer().getName());
                else
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(pu.getPlayer().getName());
                player.setDisplayName(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix() + player.getName());
                player.setPlayerListName(player.getDisplayName());
            }
            players.remove(pu);
            alivePlayers.remove(pu);
            main.boards.get(pu).setLine(2, "ßeßl…quipe ße: ßcAucune");
        } else {
            OfflinePlayer player = pu.getPlayer();
            team.removeEntry(pu.getPlayer().getName());

            if (main.isState(Gstate.WAITING) || main.isState(Gstate.STARTING)) if (pu.isHost())
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Host").addEntry(player.getName());
            else
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Joueur").addEntry(player.getName());

            players.remove(main.getPlayerUHC(player));
            alivePlayers.remove(main.getPlayerUHC(player));
        }
        pu.setTeam(null);

        Bukkit.getPluginManager().callEvent(new TeamChangeEvent(pu, this));
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

        try {
            if (team != null)
                team.unregister();
        } catch (IllegalStateException ignored) {}
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
        switch (getPrefix().symbol) {
            case Symbols.HEARTH + " ":
                bm.addPattern(new Pattern(BLACK, CIRCLE_MIDDLE));
                bm.addPattern(new Pattern(c, TRIANGLE_TOP));
                break;
            case Symbols.STARBALL + " ":
                bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
                bm.addPattern(new Pattern(BLACK, CREEPER));
                bm.addPattern(new Pattern(c, TRIANGLE_BOTTOM));
                bm.addPattern(new Pattern(BLACK, CIRCLE_MIDDLE));
                break;
            case Symbols.ARROW_RIGHT_FULL + " ":
                bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
                bm.addPattern(new Pattern(BLACK, STRIPE_LEFT));
                bm.addPattern(new Pattern(c, SQUARE_TOP_LEFT));
                bm.addPattern(new Pattern(c, SQUARE_BOTTOM_LEFT));
                bm.addPattern(new Pattern(c, BORDER));
                break;
            case Symbols.SNOWMAN + " ": {
                int d = c.ordinal();
                bm.setBaseColor(BLACK);
                DyeColor bc = DyeColor.values()[d];
                bm.addPattern(new Pattern(bc, STRIPE_TOP));
                bm.addPattern(new Pattern(bc, CURLY_BORDER));
                bm.addPattern(new Pattern(bc, CURLY_BORDER));
                bm.addPattern(new Pattern(bc, CURLY_BORDER));
                bm.addPattern(new Pattern(bc, BORDER));
                break;
            }
            case Symbols.CROSS + " ":
                bm.addPattern(new Pattern(BLACK, CROSS));
                bm.addPattern(new Pattern(c, BORDER));
                bm.addPattern(new Pattern(c, BORDER));
                bm.addPattern(new Pattern(c, CURLY_BORDER));
                break;
            case Symbols.OK + " ":
                bm.addPattern(new Pattern(BLACK, RHOMBUS_MIDDLE));
                bm.addPattern(new Pattern(c, HALF_HORIZONTAL));
                bm.addPattern(new Pattern(c, CIRCLE_MIDDLE));
                bm.addPattern(new Pattern(c, STRIPE_LEFT));
                break;
            case Symbols.NUCLEAR + " ": {
                int d = c.ordinal();
                bm.setBaseColor(BLACK);
                DyeColor bc = DyeColor.values()[d];
                bm.addPattern(new Pattern(bc, HALF_HORIZONTAL_MIRROR));
                bm.addPattern(new Pattern(BLACK, TRIANGLE_BOTTOM));
                bm.addPattern(new Pattern(bc, TRIANGLE_TOP));
                bm.addPattern(new Pattern(bc, STRIPE_TOP));
                bm.addPattern(new Pattern(bc, STRIPE_BOTTOM));
                bm.addPattern(new Pattern(bc, CURLY_BORDER));
                break;
            }
            case Symbols.INFINITE + " ":
                bm.addPattern(new Pattern(BLACK, STRIPE_BOTTOM));
                bm.addPattern(new Pattern(BLACK, STRIPE_TOP));
                bm.addPattern(new Pattern(c, RHOMBUS_MIDDLE));
                bm.addPattern(new Pattern(c, BORDER));
                bm.addPattern(new Pattern(BLACK, CROSS));
                bm.addPattern(new Pattern(c, CURLY_BORDER));
                break;
            case Symbols.CERCLED_S + " ":
                bm.addPattern(new Pattern(BLACK, STRIPE_BOTTOM));
                bm.addPattern(new Pattern(c, DIAGONAL_RIGHT_MIRROR));
                bm.addPattern(new Pattern(BLACK, STRIPE_TOP));
                bm.addPattern(new Pattern(c, RHOMBUS_MIDDLE));
                bm.addPattern(new Pattern(BLACK, STRIPE_DOWNRIGHT));
                bm.addPattern(new Pattern(c, BORDER));
                bm.addPattern(new Pattern(c, CURLY_BORDER));
                break;
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
