package fr.neyuux.uhc.config;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.ItemsStack;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameConfig implements Listener {

    private final Index main;
    private final Index.Modes mode;
    private final Set<Class<? extends Enum>> enumSet = new HashSet<>();
    private String teamType = "FFA";
    public final ArrayList<UUID> hosts = new ArrayList<>();

    public GameConfig(Index main, Index.Modes mode) {
        this.main = main;
        this.mode = mode;
        enumSet.add(GameConfig.Timers.class);
        enumSet.add(GameConfig.Drops.class);
        enumSet.add(GameConfig.DeathDrop.class);
        enumSet.add(GameConfig.CustomCrafts.class);
        enumSet.add(GameConfig.WorldRules.class);
        enumSet.add(GameConfig.MineRules.class);
        enumSet.add(GameConfig.PlayerRules.class);
    }

    public String getTeamType() {
        return teamType;
    }

    public void setTeamType(int teamType, boolean random) {
        if (teamType == 0) this.teamType = "FFA";
        else
            if (random)
                this.teamType = "Random To" + teamType;
            else
                this.teamType = "To" + teamType;
    }



    @EventHandler
    public void onInteractComparator(PlayerInteractEvent ev) {
        if (ev.getItem().equals(getComparator()))
            ev.getPlayer().openInventory(getGameConfigInv(ev.getPlayer()));
    }

    @EventHandler
    public void onInvClickComparator(InventoryClickEvent ev) {
        if (ev.getCurrentItem() != null && ev.getCurrentItem().equals(getComparator()))
            ev.getWhoClicked().openInventory(getGameConfigInv(ev.getWhoClicked()));
    }


    @EventHandler
    public void onGameConfigInv(InventoryClickEvent ev) {
        Player player = (Player)ev.getWhoClicked();
        Inventory inv = ev.getInventory();
        ItemStack current = ev.getCurrentItem();

        if (current == null) return;

        if (inv.getName().equals(getGameConfigInv(player).getName())) {
            ev.setCancelled(true);

            switch (current.getType()) {
                case ITEM_FRAME:
                    Inventory invModes = Bukkit.createInventory(null, 27, "§2§lMode §2de jeu de la Partie");
                    setInvCoin(invModes, (short)13);
                    invModes.setItem(11, new ItemsStack(Material.GOLDEN_APPLE, Index.Modes.UHC.getPrefix(), "§7Passe le mode de jeu en UHC.").toItemStack());
                    invModes.setItem(15, new ItemsStack(Material.MONSTER_EGG, Index.Modes.LG.getPrefix(), "§7Passe le mode de jeu en LG UHC").toItemStack());
                    invModes.setItem(26, getReturnArrow());
                    break;
                case BOOK_AND_QUILL:
                    player.openInventory(getScenInv());
                    break;
                case APPLE:
                    player.openInventory(getConfigInv());
                    break;
            }
        }
    }



    private Inventory getGameConfigInv(HumanEntity player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§c§lConfiguration");
        List<String> ops = new ArrayList<>();
        setInvCoin(inv, (byte)14);

        for (UUID id : hosts) ops.add(Bukkit.getPlayer(id).getDisplayName());
        System.out.println("aa");
        inv.setItem(38, new ItemsStack(Material.SIGN, "§cListe des §lConfigurateurs", ops.toArray(new String[0])).toItemStack());

        inv.setItem(30, new ItemsStack(Material.SKULL_ITEM, (short)3, "§6Joueurs", "§fPermet de gérer", "§fles joueurs", "§f§o(spectateur, etc)").toItemStackwithSkullMeta(player.getName()));

        inv.setItem(32, new ItemsStack(Material.BARRIER, "§bReset la Map", "§fPermet de reset", "§fla map.").toItemStack());

        inv.setItem(13, new ItemsStack(Material.APPLE, "§f§lParamètres de la Partie", "§fPermet de changer les", "§foptions de la partie.").toItemStack());

        inv.setItem(15, new ItemsStack(Material.BOOK_AND_QUILL, "§6§lScénarios", "§fPermet de gérer les", "§fscénarios de la partie.").toItemStack());

        inv.setItem(11, new ItemsStack(Material.ITEM_FRAME, "§2Changer le §lMode §2de jeu", "§fPermet de changer le", "§fmode de jeu de la partie.", "", "§eActuel : §c§l" + main.getPrefix()).toItemStack());

        return inv;
    }

    private Inventory getScenInv() {
        Inventory inv = Bukkit.createInventory(null, 27, "§c§lConfigAffichage §6§lScénario");
        setInvCoin(inv, (byte)4);

        List<String> activated = new ArrayList<>();
        for (Scenarios sc : Scenarios.getActivatedScenarios()) activated.add("§a§l - §a" + sc.getName());
        inv.setItem(10, new ItemsStack(Material.WOOL, (short)6, "§2Liste des Scénarios activés : ", activated.toArray(new String[0])).addGlowEffect().toItemStack());

        inv.setItem(12, new ItemsStack(Material.BOOK_AND_QUILL, "§6Modifier les Scénarios", "§7Ouvre le menu des scénarios", "§b>>Clique").toItemStack());

        inv.setItem(14, new ItemsStack(Material.WOOD_PICKAXE, "§eModes de jeux", "§7Ouvre le menu des modes de jeux", "§b>>Clique").toItemStack());
        if (mode.equals(Index.Modes.LG)) inv.setItem(14, new ItemsStack(Material.MONSTER_EGG, "§9Configurer le LG UHC", "§7Ouvre le menu de configuration du LG UHC", "§b>>Clique").toItemStack());

        return inv;
    }

    private Inventory getConfigInv() {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, "§c§lConfigAffichage §f§lGamePara");
        setInvCoin(inv, (byte)0);

        if (!mode.equals(Index.Modes.LG)) inv.setItem(38, new ItemsStack(Material.BANNER, "§eConfigurer les §lÉquipes", "§bValeurs actuelles : ", "§b§l - §eTaille des équipes §b: §e§l" + getTeamType(), "", "§7Ouvre le menu de changement", "§7de la taille des équipes.", "§b>>Clique").toItemStack());

        return inv;
    }



    public ItemStack getComparator() {
        return new ItemsStack(Material.REDSTONE_COMPARATOR, "§c§lConfiguration de la partie", "§7Permet de configurer la partie", "§b>>Clique droit").toItemStack();
    }

    public ItemStack getReturnArrow() {
        return new ItemsStack(Material.ARROW, "§cRetour", "§7Retourner au menu précédent").toItemStack();
    }


    private void setInvCoin(Inventory inv, short color) {
        ItemStack verre = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        int slot1 = 0;
        int slot2 = 8;
        int slot3 = inv.getSize() - 9;
        int slot4 = inv.getSize() - 1;
        inv.setItem((slot1), verre);
        inv.setItem((slot1 + 9), verre);
        inv.setItem((slot1 + 1), verre);
        inv.setItem((slot2), verre);
        inv.setItem((slot2 + 9), verre);
        inv.setItem((slot2 - 1), verre);
        inv.setItem((slot3), verre);
        inv.setItem((slot3 - 9), verre);
        inv.setItem((slot3 + 1), verre);
        inv.setItem((slot4), verre);
        inv.setItem((slot4 - 9), verre);
        inv.setItem((slot4 - 1), verre);
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

        public static String getEnumDisplayName() {
            return "§4Drop à la mort";
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

        public static String getEnumDisplayName() {
            return "§6Règles de Minage";
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

        public static String getEnumDisplayName() {
            return "§6Règles des Joueurs";
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

        public static String getEnumDisplayName() {
            return "§eTimers";
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

        public static String getEnumDisplayName() {
            return "§2Crafts customisés";
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

        public static String getEnumDisplayName() {
            return "§2Règles de Mondes";
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

        public static String getEnumDisplayName() {
            return "§7Drops";
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
