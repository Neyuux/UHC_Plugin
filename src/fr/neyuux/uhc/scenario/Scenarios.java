package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.ItemsStack;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.classes.*;
import fr.neyuux.uhc.scenario.classes.modes.Moles;
import fr.neyuux.uhc.scenario.classes.modes.Switch;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public enum Scenarios {

    CUT_CLEAN("CutClean", "�7�lCutClean", false, "prout", CutClean.class),
    FAST_SMELTING("FastSmelting", "�7�lFastSmelting", false, "", FastSmelting.class, createConfigInv("�7�lFastSmelting", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.COAL, "�7Rapidit� de Cuisson", "�bValeur : �a3 fois plus rapide")))), getValue("�7Rapidit� de Cuisson", 3, " fois plus rapide")),
    HASTEY_BOYS("HasteyBoys", "�e�lHasteyBoys", false, "", HasteyBoys.class, createConfigInv("�e�lHasteyBoys", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.BOOK, "�dNiveau des Enchantements", "�bValeur : �a�l3")))), getValue("�dNiveau des Enchantements", 3, "")),
    BLOOD_DIAMOND("BloodDiamond", "�4�lBlood�b�lDiamond", false, "", BloodDiamond.class, createConfigInv("�4�lBlood�b�lDiamond", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "�cD�g�ts re�us", "�bValeur : �4�l0.5" + Symbols.HEARTH)))), getValue("�cD�g�ts re�us", 0.5, Symbols.HEARTH)),
    FINAL_HEAL("Final Heal", "�d�lFinal Heal", false, "", FinalHeal.class, createConfigInv("�d�lFinal Heal", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "�eTemps d'activation", "�bValeur : �a10 minutes")))), getValue("�eTemps d'activation", 60 * 10, "timer")),

    BAREBONES("Barebones", "�e�lBarebones", false, "", BareBones.class, createConfigInv("�e�lBarebones", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND, "�bNombre de Diamants re�us", "�bValeur : �a�l1")), getEntry(1, new ItemsStack(Material.GOLDEN_APPLE, "�eNombre de Pommes en Or re�ues", "�bValeur : �a�l1")), getEntry(2, new ItemsStack(Material.ARROW, "�7Nombre de Fl�ches re�ues", "�bValeur : �a�l32")), getEntry(3, new ItemsStack(Material.STRING, "�fNombre de Fils re�us", "�bValeur : �a�l2")))), getValue("�bNombre de Diamants r��us", 1, ""), getValue("�eNombre de Golden Apple r��ues", 1, ""), getValue("�7Nombre de Fl�ches re�ues", 32, ""), getValue("�fNombre de Fils re�us", 2, "")),
    VEINMINER("Veinminer", "�5�lVeinMiner", false, "", VeinMiner.class),
    SUPER_HEROES("SuperHeroes", "�c�lSuperHeroes", false, "", SuperHeroes.class, createConfigInv("�c�lSuperHeroes", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.IRON_CHESTPLATE, "�7Effet de R�sistance", "�bValeur : �aActiv�")), getEntry(1, new ItemsStack(new ItemsStack(Material.IRON_SWORD, "�cEffet de Force", "�bValeur : �aActiv�").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES))), getEntry(0, new ItemsStack(Material.SUGAR, "�bEffet de Rapidit�", "�bValeur : �aActiv�")), getEntry(3, new ItemsStack(Material.BARRIER, "�dEffet de Double Vie", "�bValeur : �aActiv�")), getEntry(4, new ItemsStack(Material.RABBIT_FOOT, "�aEffet de Jump Boost", "�bValeur : �aActiv�")))), getValue("�7Effet de R�sistance", true, ""), getValue("�cEffet de Force", true, ""), getValue("�bEffet de Rapidit�", true, ""), getValue("�dEffet de Double Vie", true, ""), getValue("�aEffet de Jump Boost", true, "")),

    FIRE_LESS("FireLess", "�6�lFireLess", false, "", FireLess.class),
    NO_FALL("NoFall", "�f�lNoFall", false, "", NoFall.class),
    TIME_BOMB("TimeBomb", "�c�lTimeBomb", false, "", TimeBomb.class, createConfigInv("�c�lTimeBomb", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "�6Temps avant explosion", "�bValeur : �a20 secondes")))), getValue("�6Temps avant explosion", 20, "timer")),

    BOW_SWAP("BowSwap", "�5�lBowSwap", false, "", BowSwap.class, createConfigInv("�5�lBowSwap", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PEARL, "�5Pourcentage de chance de Swap", "�bValeur : �a�l30%")))), getValue("�5Pourcentage de chance de Swap", 30, "%")),
    BOOKCEPTION("Bookception", "�5�lBookCeption", false, "", Bookception.class),
    PARANOIA("Paranoia", "�5�lParanoia", false, "Sa grosse m�re la tchoin", Paranoia.class, createConfigInv("�5�lParanoia", 18, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "�bAnnonce lors du minage d'un diamant", "�bValeur : �aActiv�")), getEntry(1, new ItemsStack(Material.GOLD_INGOT, "�6Annonce lors du craft d'une Golden Head", "�bValeur : �aActiv�")), getEntry(2, new ItemsStack(Material.APPLE, "�eAnnonce lors du craft d'une GApple", "�bValeur : �aActiv�")), getEntry(3, new ItemsStack(Material.ANVIL, "�7Annonce lors du craft d'une enclume", "�bValeur : �aActiv�")), getEntry(4, new ItemsStack(Material.ENCHANTMENT_TABLE, "�dAnnonce lors du craft d'une table d'enchant", "�bValeur : �aActiv�")), getEntry(5, new ItemsStack(Material.GOLD_ORE, "�eAnnonce lors du minage d'un or", "�bValeur : �aActiv�")), getEntry(6, new ItemsStack(Material.IRON_SWORD, "�4Annonce lors d'une mort d'un joueur", "�bValeur : �aActiv�")), getEntry(7, new ItemsStack(Material.OBSIDIAN, "�5Annonce lors du passage d'un portail", "�bValeur : �aActiv�")), getEntry(8, new ItemsStack(new ItemsStack(Material.GOLDEN_APPLE, "�eAnnonce lors de l'utilisation d'une GApple", "�bValeur : �aActiv�").addGlowEffect())), getEntry(9, new ItemsStack(Material.GOLDEN_APPLE, (short)1, "�6Annonce lors de l'utilisation d'une golden head", "�bValeur : �aActiv�")))), getValue("�bAnnonce lors du minage d'un diamant", true, ""), getValue("�6Annonce lors du craft d'une Golden Head", true, ""), getValue("�eAnnonce lors du craft d'une Golden Apple", true, ""), getValue("�7Annonce lors du craft d'une enclume", true, ""), getValue("�dAnnonce lors du craft d'une table d'enchantement", true, ""), getValue("�eAnnonce lors du minage d'un or", true, ""), getValue("�4Annonce lors de la mort d'un joueur", true, ""), getValue("�5Annonce lors du passage d'un portail", true, ""), getValue("�eAnnonce lors de l'utilisation d'une GApple", true, ""), getValue("�6Annonce lors de l'utilisation d'une Golden Head", true, "")),
    NO_CLEANUP("NoCleanUP", "�c�lNoCleanUp", false, "", NoCleanUP.class, createConfigInv("�c�lNoCleanUp", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.APPLE, "�cNombre de Coeurs r�cup�r�s", "�bValeur : �a3.0" + Symbols.HEARTH)))), getValue("�cNombre de Coeurs r�cup�r�s", 3.0, Symbols.HEARTH)),

    ROD_LESS("RodLess", "�8�lRodLess", false, "", RodLess.class),
    BOW_LESS("BowLess", "�8�lBowLess", false, "", BowLess.class),
    NO_ANVIL("NoAnvil", "�8�lNoAnvil", false, "", NoAnvil.class),
    FIRE_ENCHANT_LESS("Fire Enchant Less", "�6�lFire Enchant Less", false, "", FireEnchantLess.class, createConfigInv("�6�lFire Enchant Less", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.IRON_SWORD, "�cFire Aspect", "�bValeur : �cD�sactiv�e")), getEntry(1, new ItemsStack(Material.BOW, "�7Flame", "�bValeur : D�sactiv�")))), getValue("�cFire Aspect", false, ""), getValue("�7Flame", false, "")),
    GONE_FISHING("GoneFishing", "�a�lGoneFishing", false, "", GoneFishing.class),

    TIMBER("Timber", "�6�lTimber", false, "", Timber.class),
    SKY_HIGH("SkyHigh", "�6�lSkyHigh", false, "", SkyHigh.class, createConfigInv("�6�lSkyHigh", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "�eTemps d'activation", "�bValeur : �a60 minutes")), getEntry(1, new ItemsStack(Material.APPLE, "�cNombre de coeurs perdus", "�bValeur : �4�l0.5" + Symbols.HEARTH)), getEntry(2, new ItemsStack(Material.DIRT, "�aHauteur minimale", "�bValeur : �a150 blocks")), getEntry(3, new ItemsStack(Material.WATCH, "�2Give d'EnderPearls", "�bValeur : �cAucun give")), getEntry(4, new ItemsStack(Material.NETHER_STAR, "�6Stuff drop au pied du tueur", "�bValeur : �cD�sactiv�")))), getValue("�eTemps d'activation", 60 * 60, "timer"), getValue("�cNombre de coeurs perdus", 0.5, Symbols.HEARTH), getValue("�aHauteur minimale", 150, " blocks"), getValue("�2Give d'EnderPearls", 0, ""), getValue("�6Stuff drop au pied du tueur", false, "")),
    NETHERIBUS("Netheribus", "�c�lNetheribus", false, "", Netheribus.class, createConfigInv("�c�lNetheribus", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "�eTemps d'activation", "�bValeur : �a60 minutes")), getEntry(1, new ItemsStack(Material.APPLE, "�cNombre de coeurs perdus", "�bValeur : �4�l0.5" + Symbols.HEARTH)))), getValue("�eTemps d'activation", 60 * 60, "timer"), getValue("�cNombre de coeurs perdus", 0.5, Symbols.HEARTH)),

    ANONYMOUS("Anonymous", "�7�lAnonymous", false, "", Anonymous.class),
    BEST_PVE("BestPVE", "�f�lBestPVE", false, "", BestPVE.class, createConfigInv("�f�lBestPVE", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "�eTemps entre chaque activation", "�Valeur : �a15 minutes")))), getValue("�eTemps entre chaque activation", 15 * 60, "timer")),
    NO_ENCHANT("NoEnchant", "�d�lNoEnchant", false, "", NoEnchant.class),

    FAST_GETAWAY("FastGetaway", "�b�lFastGetAway", false, "", FastGetaway.class, createConfigInv("�b�lFastGetAway", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.SUGAR, "�bNiveau de l'effet de Rapidit�", "�bValeur : �a�l2")), getEntry(1, new ItemsStack(Material.WATCH, "�eDur�e de l'effet", "�bValeur : �a�l10s")))), getValue("�bNiveau de la Rapidit�", 2, ""), getValue("�eDur�e de l'effet", 10, "timer")),
    NO_BOOK_SHELVES("NoBookShelves", "�5�lNoBookShelves", false, "", NoBookShelves.class),

    DIAMOND_LESS("DiamondLess", "�b�lDiamond�8�lLess", false, "", DiamondLess.class),
    VANILLA_PLUS("Vanilla+", "�a�lVanilla+", false, "", VanillaPlus.class, createConfigInv("�a�lVanilla+", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.APPLE, "�cMultiplicateur du drop des pommes", "�bValeur : �a�l2")), getEntry(1, new ItemsStack(Material.FLINT, "�8Multiplicateur du drop de silex", "�bValeur : �a�l2")))), getValue("�cMultiplicateur du drop des pommes", 2, ""), getValue("�8Multiplicateur du drop du silex", 2, "")),

    REWARDING_LONGSHOT("RewardingLongShot", "�e�lRewardingLongShot", false, "", RewardingLongShot.class),
    ENCHANTED_DEATH("EnchantedDeath", "�5�lEnchanted�4�lDeath", false, "", EnchantedDeath.class),

    TEAM_INVENTORY("TeamInventory", "�e�lTeamInventory", false, "", TeamInventory.class),
    NINE_SLOTS("NineSlots", "�7�lNineSlots", false, "", NineSlots.class),
    ORE_LIMITER("OreLimiter", "�f�lOre�c�lLimiter", false, "", OreLimiter.class, createConfigInv("�f�lOre�c�lLimiter", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "�bLimite de Diamants", "�bValeur : �a�l20")), getEntry(1, new ItemsStack(Material.GOLD_ORE, "�eLimite d'Ors", "�bValeur : �a�l60")), getEntry(2, new ItemsStack(Material.IRON_ORE, "�fLimite de Fers", "�bValeur : �a�l120")))), getValue("�bLimite de Diamants", 20, ""), getValue("�eLimite d'Ors", 60, ""), getValue("�fLimite de Fers", 120, "")),
    ARMOR_LIMITER("ArmorLimiter", "�7�lArmor�c�lLimiter", false, "", ArmorLimiter.class, createConfigInv("�7�lArmor�c�lLimiter", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_HELMET, "�2Limite du casque", "�bValeur : �b�lDiamant")), getEntry(1, new ItemsStack(Material.DIAMOND_CHESTPLATE, "�aLimite du plastron", "�bValeur : �b�lDiamant")), getEntry(2, new ItemsStack(Material.DIAMOND_LEGGINGS, "�bLimite du pantalon", "�bValeur : �b�lDiamant")), getEntry(3, new ItemsStack(Material.DIAMOND_BOOTS, "�1Limite des bottes", "�bValeur : �b�lDiamant")))), getValue("�2Limite du Casque", "�b�lDiamant", ""), getValue("�aLimite du Plastron", "�b�lDiamant", ""), getValue("�bLimite du Pantalon", "�b�lDiamant", ""), getValue("�1Limite des Bottes", "�b�lDiamant", "")),
    ENCHANT_LIMITER("EnchantLimiter", "�d�lEnchant�c�lLimiter", false, "", EnchantLimiter.class, createConfigInv("�d�lEnchant�c�lLimiter", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_SWORD, "�4Limite du Tranchant", "�bValeur : �a�l5")), getEntry(1, new ItemsStack(Material.DIAMOND_CHESTPLATE, "�7Limite de Protection", "�bValeur : �a�l4")), getEntry(2, new ItemsStack(Material.GOLD_BOOTS, "�bLimite de Chute Amortie", "�bValeur : �a�l4")), getEntry(3, new ItemsStack(Material.CACTUS, "�2Limite de �pine", "�bValeur : �a�l3")), getEntry(4, new ItemsStack(Material.PISTON_STICKY_BASE, "�dLimite de Recul", "�bValeur : �a�l2")), getEntry(5, new ItemsStack(Material.BOW, "�cLimite de Puissance", "�bValeur : �a�l5")), getEntry(6, new ItemsStack(Material.PISTON_BASE, "�eLimite de Frappe", "�bValeur : �a�l2")), getEntry(7, new ItemsStack(Material.ARROW, "�fLimite de Infinit�", "�bValeur : �a�l1")))), getValue("�4Limite de Tranchant", 5, ""), getValue("�7Limite de Protection", 4, ""), getValue("�bLimite de Chute Amortie", 4, ""), getValue("�2Limite de �pine", 3, ""), getValue("�dLimite de Recul", 2, ""), getValue("�cLimite de Puissance", 5, ""), getValue("�eLimite de Frappe", 2, ""), getValue("�fLimite de Infinit�", 1, "")),
    POTION_LIMITER("PotionLimiter", "�a�lPotion�c�lLimiter", false, "", PotionLimiter.class, createConfigInv("�a�lPotion�c�lLimiter", 18, Arrays.asList(getEntry(0, new ItemsStack(Material.NETHER_STALK, "�aPotions", "�bValeur : �aActiv�es")), getEntry(1, new ItemsStack(Material.GLOWSTONE_DUST, "�ePotions de Niveau II", "�bValeur : �aActiv�es")), getEntry(2, new ItemsStack(Material.REDSTONE, "�3Potions Allong�es", "�bValeur : �aActiv�es")), getEntry(3, new ItemsStack(Material.SULPHUR, "�8Potions en Splash", "�bValeur : �aActiv�es")), getEntry(4, new ItemsStack(Material.BLAZE_POWDER, "�4Potions de Force", "�bValeur : �aActiv�es")), getEntry(5, new ItemsStack(Material.SUGAR, "�bPotions de Rapidit�", "�bValeur : �aActiv�es")), getEntry(6, new ItemsStack(Material.GOLDEN_CARROT, "�9Potions de Vision Nocturne", "�bValeur : �aActiv�es")), getEntry(7, new ItemsStack(Material.RABBIT_FOOT, "�aPotions de Saut Am�lior�", "�bValeur : �aActiv�es")), getEntry(8, new ItemsStack(Material.MAGMA_CREAM, "�6Potions de R�sistance au Feu", "�bValeur : �aActiv�es")), getEntry(9, new ItemsStack(Material.RAW_FISH, "�1Potions de Respiration Aquatique", "�bValeur : �aActiv�es")), getEntry(10, new ItemsStack(Material.SPECKLED_MELON, "�cPotions de Soins Instantan�s", "�bValeur : �aActiv�es")), getEntry(11, new ItemsStack(Material.SPIDER_EYE, "�2Potions de Poison", "�bValeur : �aActiv�es")), getEntry(12, new ItemsStack(Material.GHAST_TEAR, "�dPotions de R�g�n�ration", "�bValeur : �aActiv�es")))), getValue("�aPotions", true, ""), getValue("�ePotions de Niveau II", true, ""), getValue("�3Potions Allong�es", true, ""), getValue("�8Potions en Splash", true, ""), getValue("�4Potions de Force", true, ""), getValue("�bPotins de Rapidit�", true, ""), getValue("�9Potions de Vision Nocturne", true, ""), getValue("�aPotions de Saut Am�lior�", true, ""), getValue("�6Potions de R�sistance au Feu", true, ""), getValue("�1Potions de Respiration Aquatique", true, ""), getValue("�cPotions de Soins Instantan�s", true, "")),

    MASTER_LEVEL("MasterLevel", "�a�lMasterLevel", false, "", MasterLevel.class),
    GRAVE_ROBBERS("GraveRobbers", "�7�lGraveRobbers", false, "", GraveRobbers.class),
    FLOWER_POWER("FlowerPower", "�2�lFlowerPower", false, "", FlowerPower.class),

    BLOOD_ENCHANT("BloodEnchant", "�4�lBlood�5�lEnchant", false, "", BloodEnchant.class, createConfigInv("�4�lBlood�5�lEnchant", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "�4Nombre de coeurs perdus", "�bValeur : �4�l0.5" + Symbols.HEARTH))))),
    ALONE_TOGETHER("AloneTogether", "�3�lAloneTogether", false, "", AloneTogether.class),
    TEAM_HEALTH("TeamHealth", "�d�lTeamHealth", false, "", TeamHealth.class),
    RED_ARROW("RedArrow", "�c�lRedArrow", false, "", RedArrow.class),

    KINGS("Kings", "�e�lKings", false, "", Kings.class),
    RANDOM_TEAM("Random Team", "�7�lRandom Teams", false, "", RandomTeam.class),

    TRUE_LOVE("TrueLove", "�d�lTrueLove", true, "", TrueLove.class),
    ASSAULT_AND_BATTERY("Assault & Battery", "�c�lAssault&Battery", true, "", AssaultAndBattery.class, createConfigInv("�c�lAssault&Battery", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PORTAL_FRAME, "�7Mode d'assignation automatique", "�bValeur : �aActiv�e"))))),
    MOLES("TaupeGun", "�6�lTaupe Gun", true, "", Moles.class, createConfigInv("�6�lTaupe Gun", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "�eTemps d'activation", "�bValeur : �a20 minutes")), getEntry(1, new ItemsStack(Material.RAW_FISH, (short)3, "�cNombre de taupes par �quipe de taupes", "�bValeur : �a�l3")), getEntry(2, new ItemsStack(Material.BANNER, "�cNombre de teams de taupes", "�bValeur : �a�l2")), getEntry(3, new ItemsStack(Material.CHEST, "�fKits activ�s", "�bValeur : �aAlchimiste, Mineur, Pyromane, Support, Aerien")), getEntry(4, new ItemsStack(Material.ROTTEN_FLESH, "�6Mode �lAPOCALYPSE", "�bValeur : �cD�sactiv�"))))),
    SWITCH("Switch", "�e�lSwitch", true, "", Switch.class, createConfigInv("�e�lSwitch", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.PAPER, "�fTemps du premier Switch", "�bValeur : �a20 minutes")), getEntry(1, new ItemsStack(Material.WATCH, "�eFr�quence des Switch", "�bValeur : �a15 minutes")), getEntry(2, new ItemsStack(Material.EYE_OF_ENDER, "�dD�lai al�atoire", "�bValeur : �cD�sactiv�")), getEntry(3, new ItemsStack(Material.TRAPPED_CHEST, "�6Switch de l'inventaire", "�bValeur : �cD�sactiv�")), getEntry(4, new ItemsStack(Material.STRING, "�aSwitch des solos", "�bValeur : �aActiv�")), getEntry(5, new ItemsStack(Material.TRIPWIRE_HOOK, "�2�quilibrage des �quipes", "�bValeur : �cD�sactiv�")))));


    private final String lore, displayName, name;
    private boolean isActivated;
    private final boolean isModeScenario;
    private final Class<?> classe;
    private final Inventory configInv;
    private final HashMap<String, HashMap<Object, String>> values = new HashMap<>();

    Scenarios(String name, String displayName, boolean isModeScenario, String lore, Class<? extends Scenario> classe, Inventory configInv, Map.Entry<String, HashMap<Object, String>>... values){
        this.name = name;
        this.displayName = displayName;
        this.isActivated = false;
        this.isModeScenario = isModeScenario;
        this.classe = classe;
        this.lore = lore;
        this.configInv = configInv;
        if (values != null)
            for (Map.Entry<String, HashMap<Object, String>> o : values)
                this.values.put(o.getKey(), o.getValue());
    }

    Scenarios(String name, String displayName, boolean isModeScenario, String lore, Class<? extends Scenario> classe){
        this.name = name;
        this.displayName = displayName;
        this.isActivated = false;
        this.isModeScenario = isModeScenario;
        this.classe = classe;
        this.lore = lore;
        this.configInv = null;
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

    public boolean isActivated(){
        return this.isActivated;
    }

    public void setActivated(boolean activated){
        this.isActivated = activated;
    }

    public boolean isModeScenario(){
        return this.isModeScenario;
    }

    public boolean isEditable() {
        return configInv != null;
    }

    public Class<?> getScenarioClass(){
        return classe;
    }

    public Inventory getConfigInv() {
        return configInv;
    }

    public List<String> getValues() {
        List<String> l = new ArrayList<>();
        for (Map.Entry<String, HashMap<Object, String>> en : values.entrySet()) {
            StringBuilder sb = new StringBuilder("�bValeur " + en.getKey() + " �b: " + en.getKey().substring(0, 2) + "�l");
            Object o = en.getValue().keySet().iterator().next();
            if (o instanceof Boolean)
                sb.append(GameConfig.getStringBoolean((boolean)o));
            else if (o instanceof Integer && en.getValue().get(o).equals("timer"))
                sb.append(Index.getTimer((int)o));
            else
                sb.append(o.toString()).append(en.getValue().get(o));
            l.add(sb.toString());
        }
        return l;
    }


    public static List<Scenarios> getActivatedScenarios() {
        List<Scenarios> scs = new ArrayList<>();
        for(Scenarios sc : Scenarios.values())
            if(sc.isActivated())
                scs.add(sc);
        return scs;
    }

    public static boolean haveModeScenarios() {
        for(Scenarios sc : getActivatedScenarios())
            if (sc.isModeScenario())
                return true;

        return false;
    }

    public static Scenarios getByDisplayName(String displayName) {
        for (Scenarios sc : Scenarios.values())
            if (sc.getDisplayName().equals(displayName))
                return sc;
        return null;
    }


    private static Inventory createConfigInv(String dname, int slots, List<AbstractMap.SimpleEntry<Integer, ItemsStack>> items) {
        Inventory inv;
        if (slots == 5) inv = Bukkit.createInventory(null, InventoryType.HOPPER, "�cConfig�6Scenario " + dname);
        else inv = Bukkit.createInventory(null, slots, "�cConfig�6Scenario " + dname);

        for (AbstractMap.SimpleEntry<Integer, ItemsStack> en : items)
            inv.setItem(en.getKey(), en.getValue().toItemStack());

        return inv;
    }

    private static AbstractMap.SimpleEntry<Integer, ItemsStack> getEntry(Integer o1, ItemsStack o2) {
        return new AbstractMap.SimpleEntry<>(o1, o2);
    }

    private static AbstractMap.SimpleEntry<String, HashMap<Object, String>> getValue(String k, Object v, String type) {
        HashMap<Object, String> hm = new HashMap<Object, String>();
        hm.put(v, type);
        return new AbstractMap.SimpleEntry<String, HashMap<Object, String>>(k, hm);
    }

}