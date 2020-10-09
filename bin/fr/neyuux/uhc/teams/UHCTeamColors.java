package fr.neyuux.uhc.teams;

public enum UHCTeamColors {

    RED("Red", "§c"),
    BLUE("Blue", "§9"),
    GREEN("Green", "§a"),
    LIGHT_PURPLE("Pink", "§d"),
    YELLOW("Yellow", "§e"),
    AQUA("Light Blue", "§b"),
    GRAY("Gray", "§7"),
    GOLD("Orange", "§6"),
    DARK_PURPLE("Purple", "§5"),
    DARK_RED("Dark Red", "§4"),
    DARK_BLUE("Dark Blue", "§1"),
    DARK_GREEN("Dark Green", "§2"),
    DARK_AQUA("Cyan", "§3"),
    DARK_GRAY("Dark Gray", "§8");

    private static int used;

    UHCTeamColors(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private String name;
    private String color;


    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }


    public static UHCTeamColors getNext() {
        used++;
        return UHCTeamColors.values()[used - 1];
    }

}
