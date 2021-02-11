package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.enums.Symbols;
import org.bukkit.ChatColor;

public class TeamPrefix {

    private static final String[] symbols = { Symbols.HEARTH + " ", Symbols.STARBALL + " ",
            Symbols.ARROW_RIGHT_FULL + " ", Symbols.SNOWMAN + " ", Symbols.CROSS + " ",
            Symbols.OK + " ", Symbols.NUCLEAR + " ", Symbols.INFINITE + " ", Symbols.CERCLED_S + " "};
    private final Index main;

    public final UHCTeamColors color;
    public final String symbol;

    public TeamPrefix(Index main, UHCTeamColors color, String symbol) {
        this.main = main;

        if (symbol == null)
            this.symbol = getCurrentSymbol();
        else
            this.symbol = symbol;
        if (this.symbol == null)
            throw new ArrayIndexOutOfBoundsException("le nombre maximal de teams a ete depasse");
        if (color == null)
            this.color = UHCTeamColors.getNext();
        else
            this.color = color;
    }

    public String toString() {
        return color.getColor() + symbol;
    }

    public static String getTaupePrefix(){
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "T " + ChatColor.DARK_RED;
    }

    public static String getHostPrefix() {
        return "§6[§fHost§6] §r";
    }


    private String getCurrentSymbol() {
        if (main.getUHCTeamManager().getTeams().size() < 13)
            return "";
        else if (main.getUHCTeamManager().getTeams().size() < 26)
            return symbols[0];
        else if (main.getUHCTeamManager().getTeams().size() < 39)
            return symbols[1];
        else if (main.getUHCTeamManager().getTeams().size() < 52)
            return symbols[2];
        else if (main.getUHCTeamManager().getTeams().size() < 65)
            return symbols[3];
        else if (main.getUHCTeamManager().getTeams().size() < 78)
            return symbols[4];
        else if (main.getUHCTeamManager().getTeams().size() < 91)
            return symbols[5];
        else if (main.getUHCTeamManager().getTeams().size() < 104)
            return symbols[6];
        else if (main.getUHCTeamManager().getTeams().size() < 117)
            return symbols[7];
        else if (main.getUHCTeamManager().getTeams().size() < 130)
            return symbols[8];
        return "";
    }
}
