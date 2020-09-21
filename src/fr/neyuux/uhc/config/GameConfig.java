package fr.neyuux.uhc.config;

import fr.neyuux.uhc.Index;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GameConfig implements Listener {

    private Index main;

    public GameConfig(Index main) {
        this.main = main;
    }



    @EventHandler
    public void onInteractComparator(PlayerInteractEvent ev) {
        if (ev.getItem().equals())
    }



    public ItemStack getComparator() {
        return Index.getItem(Material.REDSTONE_COMPARATOR, 1, Arrays.asList("§7Permet de configurer la partie", "§b>>Clique droit"), "§c§lConfiguration de la partie", (short)0);
    }


    public enum DeathDrop {

        HEAD("§cTête", Material.SKULL_ITEM, (short) 3, "§7Drop de la tête du joueur", false),
        GOLDEN_HEAD("§6Golden Head", Material.GOLDEN_APPLE, (short) 0, "§7Drop d'une golden head", false),
        GOLDEN_APPLE("§eGolden Apple", Material.GOLDEN_APPLE, (short) 1,  "§7Drop d'une golden apple", true),
        LIGHTNING("§fÉclair", Material.BLAZE_ROD, (short) 0,  "§7Apparition d'un éclair à la mort" , true),
        BARRIER_HEAD("§4Poteau avec la tête", Material.DARK_OAK_FENCE, (short) 0,  "§7Apparition d'un poteau avec la tête du joueur", true);

        private final String name;
        private final Material type;
        private final short data;
        private final String lore;
        private boolean isActivated;

        DeathDrop(String name, Material type, short data, String lore, boolean isActivated){
            this.name = name;
            this.type = type;
            this.data = data;
            this.lore = lore;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public Material getType(){
            return this.type;
        }

        public short getData(){
            return this.data;
        }

        public String getLore(){
            return this.lore;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean valeur){
            this.isActivated = valeur;
        }

    }

    public enum MineRules {

        ROLLERCOASTER("§6RollerCoaster", Material.COBBLESTONE_STAIRS, (short) 1, "§7Minage en escalier de la couche 32 à 6", true),
        STRIMING("§7Stripmining", Material.BEDROCK, (short) 0, "§7Minage tout droit couche 11", true),
        POKEHOLLING("§bPokeholling", Material.RAILS, (short) 0,  "§7Minage optimisé", true),
        SON_MINAGE("§aSoundMining/EntityMining", Material.NOTE_BLOCK, (short) 0, "§7Se servir du son pour miner", true),
        DIGDOWN("§cDigDown", Material.IRON_SPADE, (short) 0, "§7S'enterrer sous terre pour se cacher d'un fight", false),
        STALK("§4Stalk", Material.IRON_BOOTS, (short) 0, "§7Suivre un joueur dans les grottes", false),
        TRAPS("§eTrap", Material.TRAP_DOOR, (short) 0, "§7Faire des pièges pour tuer", false);

        private final String name;
        private final Material type;
        private final short data;
        private final String lore;
        private boolean isActivated;

        MineRules(String name, Material type, short data, String lore, boolean isActivated){
            this.name = name;
            this.type = type;
            this.data = data;
            this.lore = lore;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public Material getType(){
            return this.type;
        }

        public short getData(){
            return this.data;
        }

        public String getLore(){
            return this.lore;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean value){
            this.isActivated = value;
        }

    }

    public enum PlayerRules {

        CROSS_TEAM("§cCrossTeam", Material.IRON_HOE, "§7Alliance entre ennemis", false),
        FRIENDLYFIRE("§aFriendlyFire", Material.WOOD_SWORD, "§7Se taper entre coéquipiers", false),
        COORDS_F3("§eF3 Coords", Material.PAPER, "§7Activer les coordonnées F3", true),
        SPECTATEURS("§7Specs", Material.GLASS,  "§7Mode spectateur", true),
        SCOREBOARD_LIFE("§dVie dans le Tab", Material.APPLE, "§7Pourcentage de vie dans le tab", true);

        private final String name;
        private final Material type;
        private final String lore;
        private boolean isActivated;

        PlayerRules(String name, Material type, String lore, boolean isActivated){
            this.name = name;
            this.type = type;
            this.lore = lore;
            this.isActivated = isActivated;
        }

        public String getName(){
            return this.name;
        }

        public Material getType(){
            return this.type;
        }

        public String getLore(){
            return this.lore;
        }

        public boolean getValue(){
            return this.isActivated;
        }

        public void setValue(boolean b){
            this.isActivated = b;
        }
    }

    public enum Timers {

        INVINCIBILITY("§eInvincibilité", true, 30, Material.DIAMOND_CHESTPLATE),
        PVP("§cPvP", true, 20 * 60, Material.IRON_SWORD),
        BORDER("§9Bordure", true, 60 * 60, Material.BARRIER),
        EPISOD("§6Épisode", false, 20 * 60, Material.COMPASS);

        Timers(String name, boolean isActivated, int seconds, Material type) {
            this.name = name;
            this.isActivated = isActivated;
            this.seconds = seconds;
            this.type = type;
        }

        private final String name;
        private boolean isActivated;
        private int seconds;
        private final Material type;


        public String getName() {
            return name;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
        }

        public int getSeconds() {
            return seconds;
        }

        public Material getItemType() {
            return type;
        }


        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
    }

    public enum CustomCrafts {

        GOLDEN_HEAD("§eGolden Head", true, Material.GOLDEN_APPLE),
        DOUBLE_ARROW("§cDouble Arrow", true,Material.ARROW),
        STRING("§fString", true, Material.STRING),
        SADDLE("§6Selle", false, Material.SADDLE);

        CustomCrafts(String name, boolean isActivated, Material type) {
            this.name = name;
            this.isActivated = isActivated;
            this.type = type;
        }

        private final String name;
        private boolean isActivated;
        private final Material type;


        public String getName() {
            return name;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
        }

        public Material getItemType() {
            return type;
        }

    }

    public enum WorldRules {

        HORSE("§6Chevaux", Material.SADDLE, false),
        DAY_CYCLE("§9Cycle Jour/Nuit", Material.WATCH, false),
        TOWER("§7Towers", Material.DIRT, false),
        MILK("§fSeaux de lait", Material.MILK_BUCKET, true),
        LAVA("§6Seaux de lave", Material.LAVA_BUCKET, true),
        FLINT_AND_STEEL("§7Briquets", Material.FLINT_AND_STEEL, true),
        LAVA$NOT_OVERWORLD("§6Seaux de lave §chors Overworld", Material.NETHERRACK, false),
        FLINT_AND_STEEL$NOT_OVERWORLD("§7Briquets §chors Overworld", Material.NETHER_BRICK_STAIRS, false),
        BED$NOT_OVERWORLD("§cLits hors Overworld", Material.BED, false),
        XP_QUARTZ_NERF("§aNerf de l'XP du Quartz", Material.QUARTZ_ORE, false);

        private final String name;
        private final Material material;
        private boolean value;


        WorldRules(String name, Material material, boolean value){
            this.name = name;
            this.material = material;
            this.value = value;
        }

        public String getName(){
            return name;
        }

        public Material getMaterial(){
            return material;
        }

        public void setValue(boolean value){
            this.value = value;
        }

        public boolean getValue(){
            return value;
        }
    }

    public enum Drops {

        APPLE("§cPommes", Material.APPLE, 1, 4, 1),
        FLINT("§8Silex", Material.FLINT, 10, 100, 10),
        FEATHER("§fPlumes", Material.FEATHER, 40, 90, 5),
        LEATHER("§6Cuirs", Material.LEATHER, 40, 90, 5);

        private final String name;
        private final Material type;
        private int chance;
        private final int max;
        private final int difference;

        Drops(String name, Material type, int chance, int max, int difference){
            this.name = name;
            this.type = type;
            this.chance = chance;
            this.max = max;
            this.difference = difference;
        }

        public String getName(){
            return this.name;
        }

        public Material getType(){
            return this.type;
        }


        public Integer getChance(){
            return this.chance;
        }

        public void setChance(int valeur){
            this.chance = valeur;
        }

        public Integer getMax(){
            return this.max;
        }

        public Integer getEcart(){
            return this.difference;
        }
    }



    public String getON() {
        return "§aActivé";
    }

    public String getOFF() {
        return "§cDésactivé";
    }
}
