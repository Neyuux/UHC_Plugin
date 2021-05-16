package fr.neyuux.uhc.teams;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
import org.apache.commons.lang3.Validate;

public class TeamPrefix {

    private static final String[] symbols = { Symbols.HEARTH + " ", Symbols.STARBALL + " ",
            Symbols.ARROW_RIGHT_FULL + " ", Symbols.SNOWMAN + " ", Symbols.CROSS + " ",
            Symbols.OK + " ", Symbols.NUCLEAR + " ", Symbols.INFINITE + " ", Symbols.CERCLED_S + " "};
    private final UHC main;

    public final UHCTeamColors color;
    public String symbol;
    private boolean isTaupePrefix;
    private boolean isSuperTaupePrefix;
    public static int taupeTeams;

    public TeamPrefix(UHC main, UHCTeamColors color, String symbol) {
        this.main = main;

        if (symbol == null)
            this.symbol = getCurrentSymbol();
        else
            this.symbol = symbol;
        if (this.symbol.equals("null"))
            throw new ArrayIndexOutOfBoundsException("le nombre maximal de teams a ete depasse");
        if (color == null)
            this.color = UHCTeamColors.getNext();
        else
            this.color = color;
    }

    public String toString() {
        if (!isSuperTaupePrefix) return color.getColor() + symbol;
        else return symbol;
    }

    public static String getHostPrefix() {
        return "§6[§fHost§6] §r";
    }


    private String getCurrentSymbol() {
        if (main.getUHCTeamManager().getTeams().size() < 14)
            return "";
        else if (main.getUHCTeamManager().getTeams().size() < 28)
            return symbols[0];
        else if (main.getUHCTeamManager().getTeams().size() < 42)
            return symbols[1];
        else if (main.getUHCTeamManager().getTeams().size() < 56)
            return symbols[2];
        else if (main.getUHCTeamManager().getTeams().size() < 70)
            return symbols[3];
        else if (main.getUHCTeamManager().getTeams().size() < 84)
            return symbols[4];
        else if (main.getUHCTeamManager().getTeams().size() < 98)
            return symbols[5];
        else if (main.getUHCTeamManager().getTeams().size() < 112)
            return symbols[6];
        else if (main.getUHCTeamManager().getTeams().size() < 126)
            return symbols[7];
        else if (main.getUHCTeamManager().getTeams().size() < 140)
            return symbols[8];
        return "null";
    }

    public TeamPrefix toTaupePrefix() {
        taupeTeams++;
        isTaupePrefix = true;
        symbol = "§lTaupe " + taupeTeams + " " + color.getColor();
        return this;
    }

    public TeamPrefix toSuperTaupePrefix(UHCTeam t) {
        isTaupePrefix = true;
        isSuperTaupePrefix = true;
        if (Moles.areSuperMolesTogether) symbol = "§4§lS.§f§lTaupe ";
        else {
            Validate.notNull(t, "La team ne peut pas etre nulle");
            symbol = t.getPrefix().color.getColor() + "§lS.Taupe " + t.getPrefix().symbol.charAt(8) + " ";
        }
        return this;
    }

    public boolean isTaupePrefix() {
        return isTaupePrefix;
    }
    public boolean isSuperTaupePrefix() {
        return isSuperTaupePrefix;
    }
}
