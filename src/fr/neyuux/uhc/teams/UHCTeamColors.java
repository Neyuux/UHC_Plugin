package fr.neyuux.uhc.teams;

public enum UHCTeamColors {

    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    LIGHT_PURPLE("Pink"),
    YELLOW("Yellow"),
    AQUA("Light Blue"),
    GRAY("Gray"),
    GOLD("Orange"),
    DARK_PURPLE("Purple"),
    DARK_RED("Dark Red"),
    DARK_BLUE("Dark Blue"),
    DARK_GREEN("Dark Green"),
    DARK_AQUA("Cyan"),
    DARK_GRAY("Dark Gray");

    private static int used;

    UHCTeamColors(String name) {
        this.name = name;
    }

    private String name;


    public String getName() {
        return name;
    }


    public static UHCTeamColors getNext() {
        used++;
        return UHCTeamColors.values()[used - 1];
    }

}
