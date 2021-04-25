package fr.neyuux.uhc.teams;

import org.bukkit.DyeColor;

public enum UHCTeamColors {

    RED("Red", "Rouge", "§c", DyeColor.RED),
    BLUE("Blue", "Bleu", "§9", DyeColor.BLUE),
    GREEN("Green", "Vert", "§a", DyeColor.LIME),
    LIGHT_PURPLE("Pink", "Rose", "§d", DyeColor.PINK),
    YELLOW("Yellow", "Jaune", "§e", DyeColor.YELLOW),
    AQUA("Light Blue","Bleu Clair", "§b", DyeColor.LIGHT_BLUE),
    GRAY("Gray", "Gris", "§7", DyeColor.SILVER),
    GOLD("Orange", "Orange", "§6", DyeColor.ORANGE),
    DARK_PURPLE("Purple", "Violet", "§5", DyeColor.PURPLE),
    DARK_RED("Dark Red", "Rouge Foncé", "§4", DyeColor.RED),
    DARK_BLUE("Dark Blue", "Bleu Foncé", "§1", DyeColor.BLUE),
    DARK_GREEN("Dark Green", "Vert Foncé", "§2", DyeColor.GREEN),
    DARK_AQUA("Cyan", "Cyan", "§3", DyeColor.CYAN),
    DARK_GRAY("Dark Gray", "Gris Foncé", "§8", DyeColor.GRAY),
    WHITE("White", "Blanc", "§f", DyeColor.WHITE);

    public static int used;
    public static int taupeused;

    UHCTeamColors(String name, String displayName, String color, DyeColor dc) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.dyecolor = dc;
    }

    private final String name;
    private final String displayName;
    private final String color;
    private final DyeColor dyecolor;


    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public DyeColor getDyecolor() {
        return dyecolor;
    }


    public static UHCTeamColors getNext() {
        used++;
        if (used == UHCTeamColors.values().length)
            used = 1;
        return UHCTeamColors.values()[used - 1];
    }

    public static UHCTeamColors getTaupeNext() {
        taupeused++;
        if (taupeused == 7)
            taupeused = 1;
        return new UHCTeamColors[]{DARK_RED, DARK_PURPLE, DARK_AQUA, DARK_GRAY, DARK_GREEN, DARK_BLUE}[taupeused - 1];
    }
}
