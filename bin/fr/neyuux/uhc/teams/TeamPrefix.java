package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.enums.Symbols;
import org.bukkit.ChatColor;

import java.util.Objects;

public class TeamPrefix {

    private static final String[] symbols = { Symbols.HEARTH + " ", Symbols.STARBALL + " ",
            Symbols.ARROW_RIGHT_FULL + " ", Symbols.SNOWMAN + " ", Symbols.CROSS + " ",
            Symbols.OK + " ", Symbols.NUCLEAR + " ", Symbols.INFINITE + " ", Symbols.CERCLED_S + " "};
    private Index main;

    public final UHCTeamColors color;
    public final String symbol;

    public TeamPrefix(Index main, UHCTeamColors color, String symbol) {
        this.main = main;

        if (symbol == null)
            this.symbol = getCurrentSymbol();
        else
            this.symbol = symbol;
        this.color = Objects.requireNonNull(color, "color ne peut pas etre nul");
    }

    public String toString() {
        return color.getColor() + symbol;
    }

    public static String getTaupePrefix(){
        return ChatColor.DARK_RED.toString() + "§lT §4";
    }


    private String getCurrentSymbol() {
        if (main.getUHCTeamManager().getTeams().size() < 14)
            return symbols[0];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 2)
            return symbols[1];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 3)
            return symbols[2];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 4)
            return symbols[3];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 5)
            return symbols[4];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 6)
            return symbols[5];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 7)
            return symbols[6];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 8)
            return symbols[7];
        else if (main.getUHCTeamManager().getTeams().size() < 14 * 9)
            return symbols[8];
        return null;
    }
}
