package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.ItemsStack;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.classes.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public enum Scenarios {

    CUT_CLEAN("CutClean", "§7§lCutClean", false, false, "", CutClean.class, null),
    FAST_SMELTING("FastSmelting", "§7§lFastSmelting", false, false, "", FastSmelting.class, Scenarios.FAST_SMELTING.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.COAL, "§7Rapidité de Cuisson", "§bValeur : §a3 fois plus rapide"))))),
    HASTEY_BOYS("HasteyBoys", "§e§lHasteyBoys", false, false, "", HasteyBoys.class, Scenarios.HASTEY_BOYS.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.BOOK, "§dNiveau des Enchantements", "§bValeur : §a§l3"))))),
    BLOOD_DIAMOND("BloodDiamond", "§4§lBlood§b§lDiamond", false, false, "", BloodDiamond.class, Scenarios.BLOOD_DIAMOND.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "§cDégâts reçus", "§bValeur : §4§l0.5" + Symbols.HEARTH))))),
    FINAL_HEAL("Final Heal", "§d§lFinal Heal", false, false, "", FinalHeal.class, Scenarios.FINAL_HEAL.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a10 minutes"))))),

    BAREBONES("Barebones", "§e§lBarebones", false, false, "", BareBones.class, Scenarios.BAREBONES.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND, "§bNombre de Diamants reçus", "§bValeur : §a§l1")), getEntry(1, new ItemsStack(Material.GOLDEN_APPLE, "§eNombre de Pommes en Or reçues", "§bValeur : §a§l1")), getEntry(2, new ItemsStack(Material.ARROW, "§bNombre de Flèches reçues", "§bValeur : §a§l32")), getEntry(3, new ItemsStack(Material.STRING, "§fNombre de Fils reçus", "§bValeur : §a§l2"))))),
    VEINMINER("Veinminer", "§5§lVeinMiner", false, false, "", VeinMiner.class, null),
    SUPER_HEROES("SuperHeroes", "§c§lSuperHeroes", false, false, "", SuperHeroes.class, Scenarios.SUPER_HEROES.createConfigInv(9, Arrays.asList(getEntry(0, new ItemsStack(Material.IRON_CHESTPLATE, "§7Effet de Résistance", "§bValeur : §aActivé")), getEntry(1, new ItemsStack(new ItemsStack(Material.IRON_SWORD, "§cEffet de Force", "§bValeur : §aActivé").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES))), getEntry(0, new ItemsStack(Material.SUGAR, "§bEffet de Rapidité", "§bValeur : §aActivé")), getEntry(3, new ItemsStack(Material.BARRIER, "§dEffet de Double Vie", "§bValeur : §aActivé")), getEntry(4, new ItemsStack(Material.RABBIT_FOOT, "§aEffet de Jump Boost", "§bValeur : §aActivé"))))),

    FIRE_LESS("FireLess", "§6§lFireLess", false, false, "", FireLess.class, null),
    NO_FALL("NoFall", "§f§NoFall", false, false, "", NoFall.class, null),
    TIME_BOMB("TimeBomb", "§c§lTimeBomb", false, false, "", TimeBomb.class, Scenarios.TIME_BOMB.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§6Temps avant explosion", "§bValeur : §a20 secondes"))))),

    BOW_SWAP("BowSwap", "§5§lBowSwap", false, false, "", BowSwap.class, Scenarios.BOW_SWAP.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PEARL, "§5Pourcentage de chance de Swap", "§bValeur : §a§l30%"))))),
    BOOKCEPTION("Bookception", "§5§lBookCeption", false, false, "", Bookception.class, null),
    NO_CLEANUP("NoCleanUP", "§c§lNoCleanUp", false, false, "", NoCleanUP.class, Scenarios.NO_CLEANUP.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.APPLE, "§cNombre de Coeurs récupérés", "§bValeur : §a3.0" + Symbols.HEARTH))))),

    ROD_LESS("RodLess", "§8§lRodLess", false, false, "", RodLess.class, null),
    BOW_LESS("BowLess", "§8§lBowLess", false, false, "", BowLess.class, null),
    NO_ANVIL("NoAnvil", "§8§lNoAnvil", false, false, "", NoAnvil.class, null),
    FIRE_ENCHANT_LESS("Fire Enchant Less", "§6§lFire Enchant Less", false, false, "", FireEnchantLess.class, Scenarios.FIRE_ENCHANT_LESS.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.IRON_SWORD, "§cFire Aspect", "§bValeur : §cDésactivée")), getEntry(1, new ItemsStack(Material.BOW, "§7Flame", "§bValeur : Désactivé"))))),
    GONE_FISHING("GoneFishing", "§a§lGoneFishing", false, false, "", GoneFishing.class, null),

    TIMBER("Timber", "§6§lTimber", false, false, "", Timber.class, null),
    SKY_HIGH("SkyHigh", "§6§lSkyHigh", false, false, "", SkyHigh.class, Scenarios.SKY_HIGH.createConfigInv(9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a60 minutes")), getEntry(1, new ItemsStack(Material.APPLE, "§cNombre de coeurs perdus", "§bValeur : §4§l0.5" + Symbols.HEARTH)), getEntry(2, new ItemsStack(Material.DIRT, "§aHauteur minimale", "§bValeur : §a150 blocks")), getEntry(3, new ItemsStack(Material.WATCH, "§2Give d'EnderPearls", "§bValeur : §cAucun give")), getEntry(4, new ItemsStack(Material.NETHER_STAR, "§6Stuff drop au pied du tueur", "§bValeur : §cDésactivé"))))),
    NETHERIBUS("Netheribus", "§c§lNetheribus", false, false, "", Netheribus.class, Scenarios.NETHERIBUS.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a60 minutes")), getEntry(1, new ItemsStack(Material.APPLE, "§cNombre de coeurs perdus", "§bValeur : §4§l0.5" + Symbols.HEARTH))))),

    ASSAULT_AND_BATTERY("Assault & Battery", "§c§lAssault §f§l& §a§lBattery", false, false, "", AssaultAndBattery.class, Scenarios.ASSAULT_AND_BATTERY.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PORTAL_FRAME, "§7Mode d'assignation automatique", "§bValeur : §aActivée"))))),
    ANONYMOUS("Anonymous", "§7§lAnonymous", false, false, "", Anonymous.class, null),
    BEST_PVE("BestPVE", "§f§lBestPVE", false, false, "", BestPVE.class, Scenarios.BEST_PVE.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps entre chaque activation", "§Valeur : §a15 minutes"))))),
    NO_ENCHANT("NoEnchant", "§d§lNoEnchant", false, false, "", NoEnchant.class, null),
    PARANOIA("Paranoia", "§5§lParanoia", false, false, "", Paranoia.class, Scenarios.PARANOIA.createConfigInv(18, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "§bAnnonce lors du minage d'un diamant", "§bValeur : §aActivé")), getEntry(1, new ItemsStack(Material.GOLD_INGOT, "§bAnnonce lors du craft d'une Golden Head", "§bValeur : §aActivé")), getEntry(2, new ItemsStack(Material.APPLE, "§eAnnonce lors du craft d'une GApple", "§bValeur : §aActivé")), getEntry(3, new ItemsStack(Material.ANVIL, "§7Annonce lors du craft d'une enclume", "§bValeur : §aActivé")), getEntry(4, new ItemsStack(Material.ENCHANTMENT_TABLE, "§dAnnonce lors du craft d'une table d'enchant", "§bValeur : §aActivé")), getEntry(5, new ItemsStack(Material.GOLD_ORE, "§eAnnonce lors du minage d'un or", "§bValeur : §aActivé")), getEntry(6, new ItemsStack(Material.IRON_SWORD, "§4Annonce lors d'une mort d'un joueur", "§bValeur : §aActivé")), getEntry(7, new ItemsStack(Material.OBSIDIAN, "§5Annonce lors du passage d'un portail", "§bValeur : §aActivé")), getEntry(8, new ItemsStack(Material.GOLDEN_APPLE, "§eAnnonce lors de l'utilisation d'une GApple", "§bValeur : §aActivé").addGlowEffect()), getEntry(9, new ItemsStack(Material.GOLDEN_APPLE, (short)1, "§eAnnonce lors de l'utilisation d'une golden head", "§bValeur : §aActivé"))))),

    FAST_GETAWAY("FastGetaway", "§b§lFastGetAway", false, false, "", FastGetaway.class, Scenarios.FAST_GETAWAY.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.SUGAR, "§bNiveau de l'effet de Rapidité", "§bValeur : §a§l2")), getEntry(1, new ItemsStack(Material.WATCH, "§eDurée de l'effet", "§bValeur : §a§l10s"))))),
    NO_BOOK_SHELVES("NoBookShelves", "§5§lNoBookShelves", false, false, "", NoBookShelves.class, null),

    DIAMOND_LESS("DiamondLess", "§b§lDiamond§8§lLess", false, false, "", DiamondLess.class, null),
    VANILLA_PLUS("Vanilla+", "§a§lVanilla+", false, false, "", VanillaPlus.class, Scenarios.VANILLA_PLUS.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.APPLE, "§cMultiplicateur du drop des pommes", "§bValeur : §a§l2")), getEntry(1, new ItemsStack(Material.FLINT, "§8Multiplicateur du drop de silex", "§bValeur : §a§l2"))))),
    TRUE_LOVE("TrueLove", "§d§lTrueLove", false, false, "", TrueLove.class, null),

    REWARDING_LONGSHOT("RewardingLongShot", "§e§lRewardingLongShot", false, false, "", RewardingLongShot.class, null),
    ENCHANTED_DEATH("EnchantedDeath", "§5§lEnchanted§4§lDeath", false, false, "", EnchantedDeath.class, null),

    TEAM_INVENTORY("TeamInventory", "§e§lTeamInventory", false, false, "", TeamInventory.class, null),
    NINE_SLOTS("NineSlots", "§7§lNineSlots", false, false, "", NineSlots.class, null),
    ORE_LIMITER("OreLimiter", "§f§lOre§c§lLimiter", false, false, "", OreLimiter.class, Scenarios.ORE_LIMITER.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "§bLimite de Diamants", "§bValeur : §a§l20")), getEntry(1, new ItemsStack(Material.GOLD_ORE, "§eLimite d'Ors", "§bValeur : §a§l60")), getEntry(2, new ItemsStack(Material.IRON_ORE, "§fLimite de Fers", "§bValeur : §a§l120"))))),
    ARMOR_LIMITER("ArmorLimiter", "§7§lArmor§c§lLimiter", false, false, "", ArmorLimiter.class, Scenarios.ARMOR_LIMITER.createConfigInv(5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_HELMET, "§2Limite du casque", "§bValeur : §b§lDiamant")), getEntry(1, new ItemsStack(Material.DIAMOND_CHESTPLATE, "§aLimite du plastron", "§bValeur : §b§lDiamant")), getEntry(2, new ItemsStack(Material.DIAMOND_LEGGINGS, "§bLimite du pantalon", "§bValeur : §b§lDiamant")), getEntry(3, new ItemsStack(Material.DIAMOND_BOOTS, "§1Limite des bottes", "§bValeur : §b§lDiamant"))))),
    ENCHANT_LIMITER("EnchantLimiter", "§d§lEnchant§c§lLimiter", false, false, "", EnchantLimiter.class, Scenarios.ENCHANT_LIMITER.createConfigInv(9, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_SWORD, "§4Limite du Tranchant", "§bValeur : §a§l5")), getEntry(1, new ItemsStack(Material.DIAMOND_CHESTPLATE, "§7Limite de Protection", "§bValeur : §a§l4")), getEntry(2, new ItemsStack(Material.GOLD_BOOTS, "§bLimite de Chute Amortie", "§bValeur : §a§l4")), getEntry(3, new ItemsStack(Material.CACTUS, "§2Limite de Épine", "§bValeur : §a§l3")), getEntry(4, new ItemsStack(Material.PISTON_STICKY_BASE, "§dLimite de Recul", "§bValeur : §a§l2")), getEntry(5, new ItemsStack(Material.BOW, "§cLimite de Puissance", "§bValeur : §a§l5")), getEntry(6, new ItemsStack(Material.PISTON_BASE, "§eLimite de Frappe", "§bValeur : §a§l2")), getEntry(7, new ItemsStack(Material.ARROW, "§fLimite de Infinité", "§bValeur : §a§l1"))))),
    POTION_LIMITER("PotionLimiter", "§a§lPotion§c§lLimiter", false, false, "", PotionLimiter.class, Scenarios.POTION_LIMITER.createConfigInv(18, Arrays.asList(getEntry(0, new ItemsStack(Material.NETHER_STALK, "§aPotions", "§bValeur : §aActivées")), getEntry(1, new ItemsStack(Material.GLOWSTONE_DUST, "§ePotions de Niveau II", "§bValeur : §aActivées")), getEntry(2, new ItemsStack(Material.REDSTONE, "§3Potions Allongées", "§bValeur : §aActivées")), getEntry(3, new ItemsStack(Material.SULPHUR, "§8Potions en Splash", "§bValeur : §aActivées")), getEntry(4, new ItemsStack(Material.BLAZE_POWDER, "§4Potions de Force", "§bValeur : §aActivées")), getEntry(5, new ItemsStack(Material.SUGAR, "§bPotions de Rapidité", "§bValeur : §aActivées")), getEntry(6, new ItemsStack(Material.GOLDEN_CARROT, "§9Potions de Vision Nocturne", "§bValeur : §aActivées")), getEntry(7, new ItemsStack(Material.RABBIT_FOOT, "§aPotions de Saut Amélioré", "§bValeur : §aActivées")), getEntry(8, new ItemsStack(Material.MAGMA_CREAM, "§6Potions de Résistance au Feu", "§bValeur : §aActivées")), getEntry(9, new ItemsStack(Material.RAW_FISH, "§1Potions de Respiration Aquatique", "§bValeur : §aActivées")), getEntry(10, new ItemsStack(Material.SPECKLED_MELON, "§cPotions de Soins Instantanés", "§bValeur : §aActivées")), getEntry(11, new ItemsStack(Material.SPIDER_EYE, "§2Potions de Poison", "§bValeur : §aActivées")), getEntry(12, new ItemsStack(Material.GHAST_TEAR, "§dPotions de Régénération", "§bValeur : §aActivées"))))),

    INFINITE_ENCHANTER("InfiniteEnchanter", "§f§lInfinite§5§lEnchanter", false, false, "", InfiniteEnchanter.class, null),
    MASTER_LEVEL("MasterLevel", "§a§lMasterLevel", false, false, "", MasterLevel.class, null),
    GRAVE_ROBBERS("GraveRobbers", "§7§lGraveRobbers", false, false, "", GraveRobbers.class, null),
    FLOWER_POWER("FlowerPower", "§2§lFlowerPower", false, false, "", FlowerPower.class, null),

    BLOOD_ENCHANT("BloodEnchant", "§4§lBlood§5§lEnchant", false, false, "", BloodEnchant.class, Scenarios.BLOOD_ENCHANT.createConfigInv(5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "§4Nombre de coeurs perdus", "§bValeur : §4§l0.5" + Symbols.HEARTH))))),
    ALONE_TOGETHER("AloneTogether", "§3§lAloneTogether", false, false, "", AloneTogether.class, null),
    TEAM_HEALTH("TeamHealth", "§d§lTeamHealth", false, false, "", TeamHealth.class, null),
    RED_ARROW("RedArrow", "§c§lRedArrow", false, false, "", RedArrow.class, null),

    KINGS("Kings", "§e§lKings", false, false, "", Kings.class, null),

    MOLES("TaupeGun", "§6§lTaupe Gun", false, true, "", Moles.class, Scenarios.MOLES.createConfigInv(9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a20 minutes")), getEntry(1, new ItemsStack(Material.RAW_FISH, (short)3, "§cNombre de taupes par équipe de taupes", "§bValeur : §a§l3")), getEntry(2, new ItemsStack(Material.BANNER, "§cNombre de teams de taupes", "§bValeur : §a§l2")), getEntry(3, new ItemsStack(Material.CHEST, "§fKits activés", "§bValeur : §aAlchimiste, Mineur, Pyromane, Support, Aerien")), getEntry(4, new ItemsStack(Material.ROTTEN_FLESH, "§6Mode §lAPOCALYPSE", "§bValeur : §cDésactivé"))))),
    SWITCH("Switch", "§e§lSwitch", false, true, "", Switch.class, Scenarios.SWITCH.createConfigInv(9, Arrays.asList(getEntry(0, new ItemsStack(Material.PAPER, "§fTemps du premier Switch", "§bValeur : §a20 minutes")), getEntry(1, new ItemsStack(Material.WATCH, "§eFréquence des Switch", "§bValeur : §a15 minutes")), getEntry(2, new ItemsStack(Material.EYE_OF_ENDER, "§dDélai aléatoire", "§bValeur : §cDésactivé")), getEntry(3, new ItemsStack(Material.TRAPPED_CHEST, "§6Switch de l'inventaire", "§bValeur : §cDésactivé")), getEntry(4, new ItemsStack(Material.STRING, "§aSwitch des solos", "§bValeur : §aActivé")), getEntry(5, new ItemsStack(Material.TRIPWIRE_HOOK, "§2Équilibrage des équipes", "§bValeur : §cDésactivé")))));


    private final String lore, displayName, name;
    private boolean isActivated;
    private final boolean isRoleScenario;
    private final Class<?> classe;
    private final Inventory configInv;

    Scenarios(String name, String displayName, boolean isActivated, boolean isRoleScenario, String lore, Class<? extends Scenario> classe, Inventory configInv){
        this.name = name;
        this.displayName = displayName;
        this.isActivated = isActivated;
        this.isRoleScenario = isRoleScenario;
        this.classe = classe;
        this.lore = lore;
        this.configInv = configInv;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getScenarioLore() {
        return lore;
    }

    public boolean getActivated(){
        return this.isActivated;
    }

    public void setActivated(boolean activated){
        this.isActivated = activated;
    }

    public boolean isRoleScenario(){
        return this.isRoleScenario;
    }

    public Class<?> getScenarioClass(){
        return classe;
    }

    public Inventory getConfigInv() {
        return configInv;
    }


    public static List<Scenarios> getActivatedScenarios() {
        List<Scenarios> scs = new ArrayList<>();
        for(Scenarios sc : Scenarios.values())
            if(sc.getActivated())
                scs.add(sc);
        return scs;
    }

    public static boolean haveScenariosRole() {
        for(Scenarios sc : getActivatedScenarios())
            if (sc.isRoleScenario())
                return true;

        return false;
    }


    private Inventory createConfigInv(int slots, List<AbstractMap.SimpleEntry<Integer, ItemsStack>> items) {
        Inventory inv;
        if (slots == 5) inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§6ConfigScenario " + this.displayName);
        else inv = Bukkit.createInventory(null, slots, "§6ConfigScenario " + this.displayName);

        for (AbstractMap.SimpleEntry<Integer, ItemsStack> en : items)
            inv.setItem(en.getKey(), en.getValue().toItemStack());

        return inv;
    }

    private static AbstractMap.SimpleEntry<Integer, ItemsStack> getEntry(Integer o1, ItemsStack o2) {
        return new AbstractMap.SimpleEntry<>(o1, o2);
    }

}