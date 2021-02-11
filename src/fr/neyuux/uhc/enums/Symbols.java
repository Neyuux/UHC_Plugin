package fr.neyuux.uhc.enums;

public enum Symbols {

    ;

    static {
        Symbols.GAMING_CROSS = "\u271c";
        Symbols.HEARTH = "\u2764";
        Symbols.LITTLE_HEART = "\u2764";
        Symbols.STARBALL = "\u272a";
        Symbols.NUCLEAR = "\u2622";
        Symbols.DEATH = "\u2620";
        Symbols.ARROW = "\u27b3";
        Symbols.SUN = "\u263c";
        Symbols.SQUARE = "\u25a0";
        Symbols.RUNNING = "\u264b";
        Symbols.INFINITE = "\u221e";
        Symbols.CROSS = "\u2716";
        Symbols.OK = "\u2714";
        Symbols.STAR = "\u2606";
        Symbols.SNOWMAN = "\u2603";
        Symbols.HAND_RIGHT_FULL = "\u261b";
        Symbols.HAND_LEFT_FULL = "\u261a";
        Symbols.HAND_LEFT = "\u261c";
        Symbols.HAND_UP = "\u261d";
        Symbols.HAND_RIGHT = "\u261e";
        Symbols.HAND_DOWN = "\u261f";
        Symbols.ARROW_RIGHT_FULL = "\u27a4";
        Symbols.ARROW_RIGHT_SEMI_DOWN = "\u27a2";
        Symbols.ARROW_RIGHT_SEMI_UP = "\u27a3";
        Symbols.CERCLED_S = "\u24c8";
        Symbols.CERCLED_G = "\u24bc";
        Symbols.DOUBLE_ARROW = "\u00BB";
        Symbols.PLUS_MINUS = "\u00B1";
        Symbols.WEST_ARROW = "\u2B05";
        Symbols.NORTHWEST_ARROW = "\u2B09";
        Symbols.EAST_ARROW = "\u2B95";
        Symbols.NORTHEAST_ARROW = "\u2B08";
        Symbols.SOUTH_ARROW = "\u2B07";
        Symbols.SOUTHWEST_ARROW = "\u2B0B";
        Symbols.NORTH_ARROW = "\u2B06";
        Symbols.SOUTHEAST_ARROW = "\u2B0A";
    }
    public static String GAMING_CROSS;
    public static String HEARTH;
    public static String LITTLE_HEART;
    public static String STARBALL;
    public static String NUCLEAR;
    public static String DEATH;
    public static String ARROW;
    public static String SUN;
    public static String SQUARE;
    public static String RUNNING;
    public static String INFINITE;
    public static String CROSS;
    public static String OK;
    public static String STAR;
    public static String SNOWMAN;
    public static String HAND_RIGHT_FULL;
    public static String HAND_LEFT_FULL;
    public static String HAND_LEFT;
    public static String HAND_UP;
    public static String HAND_RIGHT;
    public static String HAND_DOWN;
    public static String ARROW_RIGHT_FULL;
    public static String ARROW_RIGHT_SEMI_DOWN;
    public static String ARROW_RIGHT_SEMI_UP;
    public static String CERCLED_S;
    public static String CERCLED_G;
    public static String DOUBLE_ARROW;
    public static String PLUS_MINUS;
    public static String WEST_ARROW;
    public static String NORTHWEST_ARROW;
    public static String EAST_ARROW;
    public static String NORTHEAST_ARROW;
    public static String SOUTH_ARROW;
    public static String SOUTHWEST_ARROW;
    public static String NORTH_ARROW;
    public static String SOUTHEAST_ARROW;

    public static Symbols getBySymbol(String s) {
        for (Symbols sy : Symbols.values())
            if (sy.equals(s))
                return sy;
        return null;
    }


}
