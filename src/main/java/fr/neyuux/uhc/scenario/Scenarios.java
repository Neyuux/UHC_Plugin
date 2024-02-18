package fr.neyuux.uhc.scenario;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.classes.*;
import fr.neyuux.uhc.scenario.classes.modes.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum Scenarios {

    CUT_CLEAN("CutClean", "§7§lCutClean", false, "La nourriture et les minerais sont automatiquement cuits.", CutClean.class),
    CAT_EYES("Cat Eyes", "§3§lCat Eyes", false, "Tous les joueurs obtiennent un effet de vision nocturne.", CatEyes.class),
    FAST_SMELTING("FastSmelting", "§7§lFastSmelting", false, "Accélère la vitesse de cuisson des fours. ", FastSmelting.class, createConfigInv("§7§lFastSmelting", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.COAL, "§7Rapidité de Cuisson", "§bValeur : §a§lScenarioField./FastSmelting.smeltSpeed fois plus rapide")))), getValue("§7Rapidité de Cuisson", "/FastSmelting.smeltSpeed", " fois plus rapide", "smeltSpeed")),
    HASTEY_BOYS("HasteyBoys", "§e§lHasteyBoys", false, "Tous les outils sont automatiquement enchantés efficacité et durabilité 2.", HasteyBoys.class, createConfigInv("§e§lHasteyBoys", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.BOOK, "§dNiveau de l'Efficacité", "§bValeur : §a§lScenarioField./HasteyBoys.enchantLevel")))), getValue("§dNiveau de l'Efficacité", "/HasteyBoys.enchantLevel", "", "enchantLevel")),
    BLOOD_DIAMOND("BloodDiamond", "§4§lBlood§b§lDiamond", false, "Miner des minerais de diamants inflige des dégâts.", BloodDiamond.class, createConfigInv("§4§lBlood§b§lDiamond", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "§cDégâts reçus", "§bValeur : §4§lScenarioField./BloodDiamond.damage " + Symbols.HEARTH)))), getValue("§cDégâts reçus", "/BloodDiamond.damage", Symbols.HEARTH, "damage")),
    FINAL_HEAL("Final Heal", "§d§lFinal Heal", false, "Tous les joueurs sont soignés au bout d'un temps donné.", FinalHeal.class, createConfigInv("§d§lFinal Heal", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a§lScenarioField./FinalHeal.timer minutes")))), getValue("§eTemps d'activation", "/FinalHeal.timer", "timer", "timer")),
    BLEEDING_SWEETS("Bleeding Sweets", "§e§lBleeding Sweets", false, "Donne des diamants, des pommes en or, des fils et des flèches au kill.", BleedingSweets.class, createConfigInv("§e§lBleeding Sweets", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND, "§bNombre de Diamants reçus", "§bValeur : §a§lScenarioField./BleedingSweets.diamonds")), getEntry(1, new ItemsStack(Material.GOLDEN_APPLE, "§eNombre de Pommes en Or reçues", "§bValeur : §a§lScenarioField./BleedingSweets.gApples")), getEntry(2, new ItemsStack(Material.ARROW, "§7Nombre de Flèches reçues", "§bValeur : §a§lScenarioField./BleedingSweets.arrows")), getEntry(3, new ItemsStack(Material.STRING, "§fNombre de Fils reçus", "§bValeur : §a§lScenarioField./BleedingSweets.strings")))), getValue("§bNombre de Diamants reçus", "/BleedingSweets.diamonds", "", "diamonds"), getValue("§eNombre de Pommes en Or reçues", "/BleedingSweets.gApples", "", "gApples"), getValue("§7Nombre de Flèches reçues", "/BleedingSweets.arrows", "", "arrows"), getValue("§fNombre de Fils reçus", "/BleedingSweets.strings", "", "strings")),
    VEINMINER("Veinminer", "§5§lVeinMiner", false, "Miner un bloc dans un filon détruit le filon entier.", VeinMiner.class),
    SUPER_HEROES("SuperHeroes", "§c§lSuperHeroes", false, "Donne un effet aléatoire entre Résistance, Force, Vitesse, Double Vie, Invisibilité et Saut Amélioré à chaque joueur de la partie.", SuperHeroes.class, createConfigInv("§c§lSuperHeroes", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.IRON_CHESTPLATE, "§7Effet de Résistance", "§bValeur : §a§lScenarioField./SuperHeroes.hasResistance")), getEntry(1, new ItemsStack(new ItemsStack(Material.IRON_SWORD, "§cEffet de Force", "§bValeur : §a§lScenarioField./SuperHeroes.hasStrength").toItemStackwithItemFlag(ItemFlag.HIDE_ATTRIBUTES))), getEntry(2, new ItemsStack(Material.SUGAR, "§bEffet de Rapidité", "§bValeur : §a§lScenarioField./SuperHeroes.hasSpeed")), getEntry(3, new ItemsStack(Material.BARRIER, "§dEffet de Double Vie", "§bValeur : §a§lScenarioField./SuperHeroes.hasDoubleHealth")), getEntry(4, new ItemsStack(Material.RABBIT_FOOT, "§aEffet de Jump Boost", "§bValeur : §a§lScenarioField./SuperHeroes.hasJumpBoost")), getEntry(5, new ItemsStack(Material.GLASS, "§3Effet d'Invisibilité", "§bValeur : §a§lScenarioField./SuperHeroes.hasInvisibility")))), getValue("§7Effet de Résistance", "/SuperHeroes.hasResistance", "", "hasResistance"), getValue("§cEffet de Force", "/SuperHeroes.hasStrength", "", "hasStrength"), getValue("§bEffet de Rapidité", "/SuperHeroes.hasSpeed", "", "hasSpeed"), getValue("§dEffet de Double Vie", "/SuperHeroes.hasDoubleHealth", "", "hasDoubleHealth"), getValue("§aEffet de Jump Boost", "/SuperHeroes.hasJumpBoost", "", "hasJumpBoost"), getValue("§3Effet d'Invisibilité", "/SuperHeroes.hasInvisibility", "", "hasInvisibility")),
    NO_FOOD("No Food", "§6§lNo Food", false, "Il est impossible de perdre de la saturation.", NoFood.class),

    FIRE_LESS("FireLess", "§6§lFireLess", false, "Tous les dégâts de feu sont annulés.", FireLess.class),
    NO_FALL("NoFall", "§f§lNoFall", false, "Tous les dégâts de chute sont anuulés.", NoFall.class),
    TIME_BOMB("TimeBomb", "§c§lTimeBomb", false, "A la mort, le stuff du joueur mort apparaît dans un coffre qui explose au bout de quelques secondes.", TimeBomb.class, createConfigInv("§c§lTimeBomb", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§6Temps avant explosion", "§bValeur : §a§lScenarioField./TimeBomb.timer secondes")))), getValue("§6Temps avant explosion", "/TimeBomb.timer", "timer", "timer")),

    BOW_SWAP("BowSwap", "§5§lBowSwap", false, "A chaque flèche touchée, le joueur qui a tiré et celui qui prend des dégâts ont un pourcentage de chance d'échanger de positions.", BowSwap.class, createConfigInv("§5§lBowSwap", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PEARL, "§5Pourcentage de chance de Swap", "§bValeur : §a§lScenarioField./BowSwap.percentage %")))), getValue("§5Pourcentage de chance de Swap", "/BowSwap.percentage", "%", "percentage")),
    BOOKCEPTION("Bookception", "§5§lBookCeption", false, "Au kill, un livre avec un enchantement aléatoire est jeté.", Bookception.class),
    PARANOIA("Paranoia", "§5§lParanoia", false, "Certaines actions effectuées par des joueurs dans la parties donnent la position du dit joueur dans le chat.", Paranoia.class, createConfigInv("§5§lParanoia", 18, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "§bAnnonce lors du minage d'un diamant", "§bValeur : §a§lScenarioField./Paranoia.hasMineDiamond")), getEntry(1, new ItemsStack(Material.GOLD_INGOT, "§6Annonce lors du craft d'une Golden Head", "§bValeur : §a§lScenarioField./Paranoia.hasCraftGoldenHead")), getEntry(2, new ItemsStack(Material.APPLE, "§eAnnonce lors du craft d'une GApple", "§bValeur : §a§lScenarioField./Paranoia.hasCraftGoldenApple")), getEntry(3, new ItemsStack(Material.ANVIL, "§7Annonce lors du craft d'une enclume", "§bValeur : §a§lScenarioField./Paranoia.hasCraftAnvil")), getEntry(4, new ItemsStack(Material.ENCHANTMENT_TABLE, "§dAnnonce lors du craft d'une table d'enchant", "§bValeur : §a§lScenarioField./Paranoia.hasCraftEnchant")), getEntry(5, new ItemsStack(Material.GOLD_ORE, "§eAnnonce lors du minage d'un or", "§bValeur : §a§lScenarioField./Paranoia.hasMineGold")), getEntry(6, new ItemsStack(Material.IRON_SWORD, "§4Annonce lors d'une mort d'un joueur", "§bValeur : §a§lScenarioField./Paranoia.hasDeath")), getEntry(7, new ItemsStack(Material.OBSIDIAN, "§5Annonce lors du passage d'un portail", "§bValeur : §a§lScenarioField./Paranoia.hasPortalTravel")), getEntry(8,new ItemsStack(Material.GOLDEN_APPLE, "§eAnnonce lors de l'utilisation d'une GApple", "§bValeur : §a§lScenarioField./Paranoia.hasUseGoldenApple")), getEntry(9, new ItemsStack(Material.GOLDEN_APPLE, (short)1, "§6Annonce lors de l'utilisation d'une golden head", "§bValeur : §a§lScenarioField./Paranoia.hasUseGoldenHead")))), getValue("§bAnnonce lors du minage d'un diamant", "/Paranoia.hasMineDiamond", "", "hasMineDiamond"), getValue("§6Annonce lors du craft d'une Golden Head", "/Paranoia.hasCraftGoldenHead", "", "hasCraftGoldenHead"), getValue("§eAnnonce lors du craft d'une GApple", "/Paranoia.hasCraftGoldenApple", "", "hasCraftGoldenApple"), getValue("§7Annonce lors du craft d'une enclume", "/Paranoia.hasCraftAnvil", "", "hasCraftAnvil"), getValue("§dAnnonce lors du craft d'une table d'enchant", "/Paranoia.hasCraftEnchant", "", "hasCraftEnchant"), getValue("§eAnnonce lors du minage d'un or", "/Paranoia.hasMineGold", "", "hasMineGold"), getValue("§4Annonce lors d'une mort d'un joueur", "/Paranoia.hasDeath", "", "hasDeath"), getValue("§5Annonce lors du passage d'un portail", "/Paranoia.hasPortalTravel", "", "hasPortalTravel"), getValue("§eAnnonce lors de l'utilisation d'une GApple", "/Paranoia.hasUseGoldenApple", "", "hasUseGoldenApple"), getValue("§6Annonce lors de l'utilisation d'une Golden Head", "/Paranoia.hasUseGoldenHead", "", "hasUseGoldenHead")),
    NO_CLEANUP("NoCleanUP", "§c§lNoCleanUp", false, "Tuer quelqu'un rend de la vie au tueur.", NoCleanUP.class, createConfigInv("§c§lNoCleanUp", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.APPLE, "§cNombre de Coeurs récupérés", "§bValeur : §a§lScenarioField./NoCleanUP.healthAdded " + Symbols.HEARTH)))), getValue("§cNombre de Coeurs récupérés", "/NoCleanUP.healthAdded", Symbols.HEARTH, "healthAdded")),
    KILL_SWITCH("KillSwitch", "§c§lKill§a§lSwitch", false, "Si un joueur en tue un autre, leurs deux inventaires sont échangés.", KillSwitch.class),

    ROD_LESS("RodLess", "§8§lRodLess", false, "Empêche l'utilisation de la canne à pêche pour le PvP.", RodLess.class),
    BOW_LESS("BowLess", "§8§lBowLess", false, "Empêche la réalisation d'arc.", BowLess.class),
    NO_ANVIL("NoAnvil", "§8§lNoAnvil", false, "Empêche la réalisation et l'utilisation d'enclume.", NoAnvil.class),
    GONE_FISHING("GoneFishing", "§a§lGoneFishing", false, "Donne une canne à pêche Chance de la mer 7 et Appât 250.", GoneFishing.class),

    TIMBER("Timber", "§6§lTimber", false, "Casser une bûche d'un arbre casse les bûches de l'arbre en entier.", Timber.class),
    SKY_HIGH("SkyHigh", "§6§lSkyHigh", false, "Après un certain temps de jeu, tous les joueurs sous une couche précise subissent des dégâts. Les joueurs commencent également avec de la terre infinie.", SkyHigh.class, createConfigInv("§6§lSkyHigh", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a§lScenarioField./SkyHigh.timer minutes")), getEntry(1, new ItemsStack(Material.APPLE, "§cNombre de coeurs perdus", "§bValeur : §4§lScenarioField./SkyHigh.damage " + Symbols.HEARTH)), getEntry(2, new ItemsStack(Material.DIRT, "§aHauteur minimale", "§bValeur : §a§lScenarioField./SkyHigh.highMin blocks")), getEntry(3, new ItemsStack(Material.ENDER_PEARL, "§2Give d'EnderPearls", "§bValeur : §a§lScenarioField./SkyHigh.enderpearlGives")), getEntry(4, new ItemsStack(Material.NETHER_STAR, "§6Stuff drop au pied du tueur", "§bValeur : §a§lScenarioField./SkyHigh.hasStuffDropOnKiller")))), getValue("§eTemps d'activation", "/SkyHigh.timer", "timer", "timer"), getValue("§cNombre de coeurs perdus", "/SkyHigh.damage", Symbols.HEARTH, "damage"), getValue("§aHauteur minimale", "/SkyHigh.highMin", " blocks", "highMin"), getValue("§2Give d'EnderPearls", "/SkyHigh.enderpearlGives", "", "enderpearlGives"), getValue("§6Stuff drop au pied du tueur", "/SkyHigh.hasStuffDropOnKiller", "", "hasStuffDropOnKiller")),
    NETHERIBUS("Netheribus", "§c§lNetheribus", false, "Après un certain temps de jeu, tous les joueurs qui ne sont pas dans le Nether subissent des dégâts.", Netheribus.class, createConfigInv("§c§lNetheribus", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a§lScenarioField./Netheribus.timer minutes")), getEntry(1, new ItemsStack(Material.APPLE, "§cNombre de coeurs perdus", "§bValeur : §4§lScenarioField./Netheribus.damage " + Symbols.HEARTH)))), getValue("§eTemps d'activation", "/Netheribus.timer", "timer", "timer"), getValue("§cNombre de coeurs perdus", "/Netheribus.damage", Symbols.HEARTH, "damage")),

    ANONYMOUS("Anonymous", "§7§lAnonymous", false, "Tous les joueurs de la partie sont anonymes : leurs skins et leurs pseudos sont changés.", Anonymous.class),
    BEST_PVE("BestPVE", "§f§lBestPVE", false, "Au début de la partie, les joueurs sont ajoutés à une liste appelée \"Best PvE\". Tant que que vous êtes sur cette liste, vous gagnerez un coeur supplémentaire tous les §e§lX §7temps. Un joueur qui subit un dégât est supprimé de cette liste. Si un joueur fait un kill, il est rajouté à la liste.", BestPVE.class, createConfigInv("§f§lBestPVE", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps entre chaque activation", "§bValeur : §a§lScenarioField./BestPVE.timer minutes")))), getValue("§eTemps entre chaque activation", "/BestPVE.timer", "timer", "timer")),
    NO_ENCHANT("NoEnchant", "§d§lNoEnchant", false, "Empêche la réalisation et l'utilisation de table d'enchantement.", NoEnchant.class),
    XP_BOOST("XPBoost", "§a§lXPBoost", false, "Multiplie l'expérience reçue par les joueurs.", XPBoost.class, createConfigInv("§a§lXPBoost", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.EXP_BOTTLE, "§aMultiplicateur d'expérience", "§bValeur : §a§lScenarioField./XPBoost.multiplier %")))), getValue("§aMultiplicateur d'expérience", "/XPBoost.multiplier", "%", "multiplier")),

    FAST_GETAWAY("FastGetaway", "§b§lFastGetAway", false, "Après un kill, le tueur reçoit un effet Vitesse.", FastGetaway.class, createConfigInv("§b§lFastGetAway", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.SUGAR, "§bNiveau de l'effet de Rapidité", "§bValeur : §a§lScenarioField./FastGetaway.level")), getEntry(1, new ItemsStack(Material.WATCH, "§eDurée de l'effet", "§bValeur : §a§lScenarioField./FastGetaway.duration secondes")))), getValue("§bNiveau de l'effet de Rapidité", "/FastGetaway.level", "", "level"), getValue("§eDurée de l'effet", "/FastGetaway.duration", "timer", "duration")),
    NO_BOOK_SHELVES("NoBookShelves", "§5§lNoBookShelves", false, "Empêche la réalisation et l'utilisation de bibliothèques.", NoBookShelves.class),

    DIAMOND_LESS("DiamondLess", "§b§lDiamond§8§lLess", false, "Empêche le minage et la récupération de diamants.", DiamondLess.class),
    VANILLA_PLUS("Vanilla+", "§a§lVanilla+", false, "Les pommes et les silex sont multipliés.", VanillaPlus.class, createConfigInv("§a§lVanilla+", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.APPLE, "§cMultiplicateur du drop des pommes", "§bValeur : §a§lScenarioField./VanillaPlus.apples")), getEntry(1, new ItemsStack(Material.FLINT, "§8Multiplicateur du drop de silex", "§bValeur : §a§lScenarioField./VanillaPlus.flints")))), getValue("§cMultiplicateur du drop des pommes", "/VanillaPlus.apples", "", "apples"), getValue("§8Multiplicateur du drop du silex", "/VanillaPlus.flints", "", "flints")),

    REWARDING_LONGSHOT("RewardingLongShot", "§e§lRewardingLongShot", false, "Pour chaque enemi touché suite à un tir à longue distance, vous obtiendrez une récompense.", RewardingLongShot.class),
    ENCHANTED_DEATH("EnchantedDeath", "§5§lEnchanted§4§lDeath", false, "La réalisation de table d'enchantement est impossible. Le seul moyen d'en obtenir est de la récupérer sur le corps d'un joueur.", EnchantedDeath.class),
    TARGET("Target", "§c§lTarget", false, "A un moment dans la partie, un joueur sera désigné comme Target. La Target possède des effets et le but des autres joueurs est de la tuer. Tuer la Target vous fait devenir Target.", Target.class),

    TEAM_INVENTORY("TeamInventory", "§e§lTeamInventory", false, "Ajoute une commande §b§l/uhc ti §7qui permet de stocker des objets pour l'équipe.", TeamInventory.class),
    NINE_SLOTS("NineSlots", "§7§lNineSlots", false, "Oblige les joueurs à utiliser uniquement les 9 slots de leurs hotbar.", NineSlots.class),
    ORE_LIMITER("OreLimiter", "§f§lOre§c§lLimiter", false, "Limite le nombre de minerais pouvant être minés.", OreLimiter.class, createConfigInv("§f§lOre§c§lLimiter", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_ORE, "§bLimite de Diamants", "§bValeur : §a§lScenarioField./OreLimiter.diamonds")), getEntry(1, new ItemsStack(Material.GOLD_ORE, "§eLimite d'Ors", "§bValeur : §a§lScenarioField./OreLimiter.golds")), getEntry(2, new ItemsStack(Material.IRON_ORE, "§fLimite de Fers", "§bValeur : §a§lScenarioField./OreLimiter.irons")))), getValue("§bLimite de Diamants", "/OreLimiter.diamonds", "", "diamonds"), getValue("§eLimite d'Ors", "/OreLimiter.golds", "", "golds"), getValue("§fLimite de Fers", "/OreLimiter.irons", "", "irons")),
    ARMOR_LIMITER("ArmorLimiter", "§7§lArmor§c§lLimiter", false, "Limite l'armure pouvant être portée pour chaque pièce.", ArmorLimiter.class, createConfigInv("§7§lArmor§c§lLimiter", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.LEATHER_HELMET, "§2Limite du Casque", "§bValeur : §b§lScenarioField./ArmorLimiter.helmetMax", "", "§a>>Clic gauche pour augmenter", "§c>>Clic droit pour retirer")), getEntry(1, new ItemsStack(Material.GOLD_CHESTPLATE, "§aLimite du Plastron", "§bValeur : §b§lScenarioField./ArmorLimiter.chestplateMax", "", "§a>>Clic gauche pour augmenter", "§c>>Clic droit pour retirer")), getEntry(2, new ItemsStack(Material.IRON_LEGGINGS, "§bLimite du Pantalon", "§bValeur : §b§lScenarioField./ArmorLimiter.leggingsMax", "", "§a>>Clic gauche pour augmenter", "§c>>Clic droit pour retirer")), getEntry(3, new ItemsStack(Material.DIAMOND_BOOTS, "§1Limite des Bottes", "§bValeur : §b§lScenarioField./ArmorLimiter.bootsMax", "", "§a>>Clic gauche pour augmenter", "§c>>Clic droit pour retirer")), getEntry(4, new ItemsStack(Material.DIAMOND_BLOCK, "§3Limite de pièces en Diamant", "§bValeur : §b§lScenarioField./ArmorLimiter.diamondMax")))), getValue("§2Limite du Casque", "/ArmorLimiter.helmetMax", "", "helmetMax"), getValue("§aLimite du Plastron", "/ArmorLimiter.chestplateMax", "", "chestplateMax"), getValue("§bLimite du Pantalon", "/ArmorLimiter.leggingsMax", "", "leggingsMax"), getValue("§1Limite des Bottes", "/ArmorLimiter.bootsMax", "", "bootsMax"), getValue("§3Limite de pièces en Diamant", "/ArmorLimiter.diamondMax", "", "diamondMax")),
    ENCHANT_LIMITER("EnchantLimiter", "§d§lEnchant§c§lLimiter", false, "Limite les enchantements pouvant être mis sur les objets.", EnchantLimiter.class, createConfigInv("§d§lEnchant§c§lLimiter", 18, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND_SWORD, "§4Limite de Tranchant en §bDiamant", "§bValeur : §a§lScenarioField./EnchantLimiter.sharpnessDiamMax")), getEntry(1, new ItemsStack(Material.DIAMOND_CHESTPLATE, "§7Limite de Protection en §bDiamant", "§bValeur : §a§lScenarioField./EnchantLimiter.protectionDiamMax")), getEntry(2, new ItemsStack(Material.GOLD_BOOTS, "§bLimite de Chute Amortie", "§bValeur : §a§lScenarioField./EnchantLimiter.featherfallingMax")), getEntry(3, new ItemsStack(Material.CACTUS, "§2Limite de Épine", "§bValeur : §a§lScenarioField./EnchantLimiter.thornsMax")), getEntry(4, new ItemsStack(Material.PISTON_STICKY_BASE, "§dLimite de Recul", "§bValeur : §a§lScenarioField./EnchantLimiter.knockbackMax")), getEntry(5, new ItemsStack(Material.BOW, "§cLimite de Puissance", "§bValeur : §a§lScenarioField./EnchantLimiter.powerMax")), getEntry(6, new ItemsStack(Material.PISTON_BASE, "§eLimite de Frappe", "§bValeur : §a§lScenarioField./EnchantLimiter.punchMax")), getEntry(7, new ItemsStack(Material.ARROW, "§fLimite de Infinité", "§bValeur : §a§lScenarioField./EnchantLimiter.infinityMax")), getEntry(8, new ItemsStack(Material.IRON_SWORD, "§4Limite de Tranchant en §fFer", "§bValeur : §a§lScenarioField./EnchantLimiter.sharpnessIronMax")), getEntry(9, new ItemsStack(Material.IRON_CHESTPLATE, "§7Limite de Protection en §fFer", "§bValeur : §a§lScenarioField./EnchantLimiter.protectionIronMax")))), getValue("§4Limite de Tranchant en §bDiamant", "/EnchantLimiter.sharpnessDiamMax", "", "sharpnessDiamMax"), getValue("§7Limite de Protection en §bDiamant", "/EnchantLimiter.protectionDiamMax", "", "protectionDiamMax"), getValue("§bLimite de Chute Amortie", "/EnchantLimiter.featherfallingMax", "", "featherfallingMax"), getValue("§2Limite de Épine", "/EnchantLimiter.thornsMax", "", "thornsMax"), getValue("§dLimite de Recul", "/EnchantLimiter.knockbackMax", "", "knockbackMax"), getValue("§cLimite de Puissance", "/EnchantLimiter.powerMax", "", "powerMax"), getValue("§eLimite de Frappe", "/EnchantLimiter.punchMax", "", "punchMax"), getValue("§fLimite de Infinité", "/EnchantLimiter.infinityMax", "", "infinityMax"), getValue("§4Limite de Tranchant en §fFer", "/EnchantLimiter.sharpnessIronMax", "", "sharpnessIronMax"), getValue("§7Limite de Protection en §fFer", "/EnchantLimiter.protectionIronMax", "", "protectionIronMax")),
    POTION_LIMITER("PotionLimiter", "§a§lPotion§c§lLimiter", false, "Empêche la réalisation de certaines potions.", PotionLimiter.class, createConfigInv("§a§lPotion§c§lLimiter", 18, Arrays.asList(getEntry(0, new ItemsStack(Material.NETHER_STALK, "§aPotions", "§bValeur : §a§lScenarioField./PotionLimiter.hasPotions")), getEntry(1, new ItemsStack(Material.GLOWSTONE_DUST, "§ePotions de Niveau II", "§bValeur : §a§lScenarioField./PotionLimiter.hasLevel2Potions")), getEntry(2, new ItemsStack(Material.REDSTONE, "§3Potions Allongées", "§bValeur : §a§lScenarioField./PotionLimiter.hasExtendedPotions")), getEntry(3, new ItemsStack(Material.SULPHUR, "§8Potions en Splash", "§bValeur : §a§lScenarioField./PotionLimiter.hasSplash")), getEntry(4, new ItemsStack(Material.BLAZE_POWDER, "§4Potions de Force", "§bValeur : §a§lScenarioField./PotionLimiter.hasStrength")), getEntry(5, new ItemsStack(Material.SUGAR, "§bPotions de Rapidité", "§bValeur : §a§lScenarioField./PotionLimiter.hasSpeed")), getEntry(6, new ItemsStack(Material.GOLDEN_CARROT, "§9Potions de Vision Nocturne", "§bValeur : §a§lScenarioField./PotionLimiter.hasNightVision")), getEntry(7, new ItemsStack(Material.RABBIT_FOOT, "§aPotions de Saut Amélioré", "§bValeur : §a§lScenarioField./PotionLimiter.hasJumpBoost")), getEntry(8, new ItemsStack(Material.MAGMA_CREAM, "§6Potions de Résistance au Feu", "§bValeur : §a§lScenarioField./PotionLimiter.hasFireResistance")), getEntry(9, new ItemsStack(Material.RAW_FISH, "§1Potions de Respiration Aquatique", "§bValeur : §a§lScenarioField./PotionLimiter.hasWaterBreathing")), getEntry(10, new ItemsStack(Material.SPECKLED_MELON, "§cPotions de Soins Instantanés", "§bValeur : §a§lScenarioField./PotionLimiter.hasHeal")), getEntry(11, new ItemsStack(Material.SPIDER_EYE, "§2Potions de Poison", "§bValeur : §a§lScenarioField./PotionLimiter.hasPoison")), getEntry(12, new ItemsStack(Material.GHAST_TEAR, "§dPotions de Régénération", "§bValeur : §a§lScenarioField./PotionLimiter.hasRegeneration")))), getValue("§aPotions", "/PotionLimiter.hasPotions", "", "hasPotions"), getValue("§ePotions de Niveau II", "/PotionLimiter.hasLevel2Potions", "", "hasLevel2Potions"), getValue("§3Potions Allongées", "/PotionLimiter.hasExtendedPotions", "", "hasExtendedPotions"), getValue("§8Potions en Splash", "/PotionLimiter.hasSplash", "", "hasSplash"), getValue("§4Potions de Force", "/PotionLimiter.hasStrength", "", "hasStrength"), getValue("§bPotions de Rapidité", "/PotionLimiter.hasSpeed", "", "hasSpeed"), getValue("§9Potions de Vision Nocturne", "/PotionLimiter.hasNightVision", "", "hasNightVision"), getValue("§aPotions de Saut Amélioré", "/PotionLimiter.hasJumpBoost", "", "hasJumpBoost"), getValue("§6Potions de Résistance au Feu", "/PotionLimiter.hasFireResistance", "", "hasFireResistance"), getValue("§1Potions de Respiration Aquatique", "/PotionLimiter.hasWaterBreathing", "", "hasWaterBreathing"), getValue("§cPotions de Soins Instantanés", "/PotionLimiter.hasHeal", "", "hasHeal"), getValue("§2Potions de Poison", "/PotionLimiter.hasPoison", "", "hasPoison"), getValue("§dPotions de Régénération", "/PotionLimiter.hasRegeneration", "", "hasRegeneration")),

    MASTER_LEVEL("MasterLevel", "§a§lMasterLevel", false, "Chaque joueur commence la partie avec 10000 niveaux.", MasterLevel.class),
    GRAVE_ROBBERS("GraveRobbers", "§7§lGraveRobbers", false, "A la mort d'un joueur, une tombe est créé avec son stuff dans un coffre à l'intérieur de celle-ci.", GraveRobbers.class),
    FLOWER_POWER("FlowerPower", "§2§lFlowerPower", false, "Lorsqu'une fleur est cassée, le joueur qu'il l'a cassée reçoit un objet aléatoire.", FlowerPower.class),
    TRUITE("Truite", "§b§lTruite", false, "Il est possible de pêcher des poissons enchantés. Lorsque vous en mangez un, vous recevez un effet aléatoire. Manger un poisson annule l'effet précédent.", Truite.class),

    BLOOD_ENCHANT("BloodEnchant", "§4§lBlood§5§lEnchant", false, "Enchanter un objet fait perdre des coeurs.", BloodEnchant.class, createConfigInv("§4§lBlood§5§lEnchant", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.REDSTONE, "§4Nombre de coeurs perdus", "§bValeur : §4§lScenarioField./BloodEnchant.damage " + Symbols.HEARTH)))), getValue("§4Nombre de coeurs perdus", "/BloodEnchant.damage", Symbols.HEARTH, "damage")),
    ALONE_TOGETHER("AloneTogether", "§3§lAloneTogether", false, "Il est impossible de voir ses coéquipiers.", AloneTogether.class),
    TEAM_HEALTH("TeamHealth", "§d§lTeamHealth", false, "La vie au dessus des joueurs et dans le tab est remplacée par le nombre de coeurs de toute l'équipe.", TeamHealth.class),
    RED_ARROW("RedArrow", "§c§lRedArrow", false, "Lorsque qu'un joueur meurt, une flèche apparaît dans le ciel, pointant le lieu de la mort.", RedArrow.class),

    KINGS("Kings", "§e§lKings", false, "Un joueur dans chaque équipe sera aléatoirement désigné Roi. Il obtiendra force, résistance, vitesse, résistance au feu, hâte et double vie. S'il meurt tous les autres joueurs receveront un effet de poison puissant.", Kings.class),
    RANDOM_TEAM("Random Team", "§7§lRandom Teams", false, "Toutes les équipes seront aléatoirement constituées de joueurs de la partie.", RandomTeam.class),

    TRUE_LOVE("TrueLove", "§d§lTrueLove", true, "Chaque joueur de la partie sera seul jusqu'à la rencontre d'un autre joueur avec lequel il fera équipe. Si un joueur d'une équipe meurt, le joueur restant redeviendra seul jusqu'à trouver un nouveau coéquipier.", TrueLove.class, createConfigInv("§d§lTrueLove", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.BANNER, "§eTaille des équipes de base", "§bValeur : §e§lScenarioField./@TrueLove.teamSize")))), getValue("§eTaille des équipes de base", "/@TrueLove.teamSize", "", "teamSize")),
    ASSAULT_AND_BATTERY("Assault & Battery", "§c§lAssault&Battery", true, "Mode de jeu en équipe de 2 dont un premier joueur de l'équipe sera obligé de jouer à l'épée et l'autre à l'arc. Si un joueur est seul, alors il peut utiliser les deux.", AssaultAndBattery.class, createConfigInv("§c§lAssault&Battery", 5, Collections.singletonList(getEntry(0, new ItemsStack(Material.ENDER_PORTAL_FRAME, "§7Mode d'assignation automatique", "§bValeur : §a§lScenarioField./@AssaultAndBattery.hasRandomChoice")))), getValue("§7Mode d'assignation automatique", "/@AssaultAndBattery.hasRandomChoice", "", "hasRandomChoice")),
    SWITCH("Switch", "§e§lSwitch", true, "Tous les §e§lX §7temps, un joueur de chaque équipe sera échangé avec un autre joueur. L'inventaire (sauf la hotbar) peut également changer.", Switch.class, createConfigInv("§e§lSwitch", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.PAPER, "§fTemps du premier Switch", "§bValeur : §a§lScenarioField./@Switch.firstSwitch minutes")), getEntry(1, new ItemsStack(Material.WATCH, "§eFréquence des Switch", "§bValeur : §a§lScenarioField./@Switch.switchFrequency minutes")), getEntry(2, new ItemsStack(Material.EYE_OF_ENDER, "§dDélai aléatoire", "§bValeur : §a§lScenarioField./@Switch.randomTimeLimit minutes")), getEntry(3, new ItemsStack(Material.TRAPPED_CHEST, "§6Switch de l'inventaire", "§bValeur : §a§lScenarioField./@Switch.hasInvSwitch")), getEntry(4, new ItemsStack(Material.STRING, "§aSwitch des solos", "§bValeur : §a§lScenarioField./@Switch.hasSoloSwitch")), getEntry(5, new ItemsStack(Material.TRIPWIRE_HOOK, "§2Équilibrage des équipes", "§bValeur : §a§lScenarioField./@Switch.hasTeamBalancing")))), getValue("§fTemps du premier Switch", "/@Switch.firstSwitch", "timer", "firstSwitch"), getValue("§eFréquence des Switch", "/@Switch.switchFrequency", "timer", "switchFrequency"), getValue("§dDélai aléatoire", "/@Switch.randomTimeLimit", "timer", "randomTimeLimit"), getValue("§6Switch de l'inventaire", "/@Switch.hasInvSwitch", "", "hasInvSwitch"), getValue("§aSwitch des solos", "/@Switch.hasSoloSwitch", "", "hasSoloSwitch"), getValue("§2Équilibrage des équipes", "/@Switch.hasTeamBalancing", "", "hasTeamBalancing")),
    MOLES("TaupeGun", "§6§lTaupe Gun", true, "Au bout d'un certain temps, un joueur de chaque équipe sera désigné taupe. Il recevra un kit et devra gagner avec une nouvelle équipe composée de taupes d'autres équipes.", Moles.class, createConfigInv("§6§lTaupe Gun", 9, Arrays.asList(getEntry(0, new ItemsStack(Material.WATCH, "§eTemps d'activation", "§bValeur : §a§lScenarioField./@Moles.timer minutes")), getEntry(1, new ItemsStack(Material.RAW_FISH, (short)3, "§cNombre de taupes par équipe", "§bValeur : §a§lScenarioField./@Moles.molePerTeams")), getEntry(2, new ItemsStack(Material.BANNER, "§cNombre de teams de taupes", "§bValeur : §a§lScenarioField./@Moles.moleTeams")), getEntry(3, new ItemsStack(Material.CHEST, "§fKits activés", "§bValeur : §a§lScenarioField./@Moles.activatedKits")), getEntry(4, new ItemsStack(Material.IRON_DOOR, "§4Super Taupes", "§bValeur : §a§lScenarioField./@Moles.superMoles")), getEntry(5, new ItemsStack(Material.ROTTEN_FLESH, "§6Mode §lAPOCALYPSE", "§bValeur : §a§lScenarioField./@Moles.hasApocalypse")))), getValue("§eTemps d'activation", "/@Moles.timer", "timer", "timer"), getValue("§cNombre de taupes par équipe", "/@Moles.molePerTeams", "", "molePerTeams"), getValue("§cNombre de teams de taupes", "/@Moles.moleTeams", " équipes", "moleTeams"), getValue("§fKits activés", "/@Moles.activatedKits", "", "activatedKits"), getValue("§4Super Taupes", "/@Moles.superMoles", "", "superMoles"), getValue("§6Mode §lAPOCALYPSE", "/@Moles.hasApocalypse", "", "hasApocalypse")),
    SLAVE_MARKET("Slave Market", "§8§lSlave §a§lMarket", true, "Au début de la partie, certains joueurs deviendront des acheteurs. Ils recevront des diamants et devront gagner des coéquipiers dans une vente aux enchères. Les diamants restants seront donnés aux acheteurs au début de la partie.", SlaveMarket.class, createConfigInv("§8§lSlave §a§lMarket", 5, Arrays.asList(getEntry(0, new ItemsStack(Material.DIAMOND, "§bNombre de Diamants", "§bValeur : §a§lScenarioField./@SlaveMarket.diamonds diamants")), getEntry(1, new ItemsStack(Material.BANNER, (short)15, "§eNombre d'acheteurs", "§bValeur : §a§lScenarioField./@SlaveMarket.nOwners")), getEntry(2, new ItemsStack(Material.ENDER_PORTAL_FRAME, "§7Choix automatique des acheteurs", "§bValeur : §a§lScenarioField./@SlaveMarket.randomChoiceOwners")))), getValue("§bNombre de Diamants", "/@SlaveMarket.diamonds", " diamants", "diamonds"), getValue("§eNombre d'acheteurs", "/@SlaveMarket.nOwners", "", "nOwners"), getValue("§7Choix automatique des acheteurs", "/@SlaveMarket.randomChoiceOwners", "", "randomChoiceOwners")),
    SKY_DEFENDER("Sky Defender", "§b§lSky §c§lDefender", true, "Les défenseurs doivent défendre une bannière dans leur château. Les attaquants sont seuls et doivent décrocher la bannière après avoir tué tous les défenseurs.", SkyDefender.class);


    private final String lore, displayName, name;
    private boolean isActivated;
    private final boolean isModeScenario;
    private final Class<?> classe;
    private final Inventory configInv;
    private final HashMap<String, Map.Entry<String, String>> values = new HashMap<>();

    @SafeVarargs
    Scenarios(String name, String displayName, boolean isModeScenario, String lore, Class<? extends Scenario> classe, Inventory configInv, Map.Entry<Map.Entry<String, String>, Map.Entry<String, String>>... values){
        this.name = name;
        this.displayName = displayName;
        this.isActivated = false;
        this.isModeScenario = isModeScenario;
        this.classe = classe;
        this.lore = lore;
        this.configInv = configInv;
        if (values != null)
            for (Map.Entry<Map.Entry<String, String>, Map.Entry<String, String>> o : values) {
                this.values.put(o.getKey().getKey(), o.getValue());
                try {
                    classe.getMethod("addCache", String.class, String.class, Class.class).invoke(classe.newInstance(), o.getKey().getValue(), o.getKey().getKey(), classe);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                    Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cEchec du chargement des scénarios.");
                }
            }
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
        Inventory inv;
        if (configInv.getSize() != 5) inv = Bukkit.createInventory(configInv.getHolder(), configInv.getSize(), configInv.getName());
        else inv = Bukkit.createInventory(configInv.getHolder(), InventoryType.HOPPER, configInv.getName());
        int i = 0;
        for (ItemStack ciit : configInv.getContents()) {
            if (ciit != null && !ciit.getType().equals(Material.AIR)) {
                ItemStack it = ciit.clone();
                ItemMeta itm = it.getItemMeta();
                List<String> lore = new ArrayList<>();
                for (String s : itm.getLore())
                    if (s.contains("ScenarioField")) {
                        String ns = s;
                        ns = ns.replace("ScenarioField.", "");
                        Class<? extends Scenario> sc;
                        Object o;
                        try {
                            String getField = ns.split("\\.")[1].split(" ")[0];
                            String getClass = ns.split("\\.")[0].substring(15).replace("/", "fr.neyuux.uhc.scenario.classes.").replace("@", "modes.");
                            sc = (Class<? extends Scenario>) Class.forName(getClass);
                            o = sc.getField(getField).get(sc.newInstance());
                            if (ns.contains("minutes") && ns.split("\\.")[1].split(" ")[1].contains("minutes")) o = ((int)o) / 60;
                            ns = ns.replace("/" + sc.getSimpleName() + ".", "").replace(sc.getField(getField).getName(), o.toString());
                            ns = ns.replace("/@" + sc.getSimpleName() + ".", "").replace(sc.getField(getField).getName(), o.toString());
                            ns = ns.replace("false", GameConfig.getOFF());
                            ns = ns.replace("true", GameConfig.getON());
                        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | ClassNotFoundException e) {
                            e.printStackTrace();
                            Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cEchec du chargement des valeurs du scénario " + configInv.getName() + " 2");
                        }
                        lore.add(ns);
                    } else lore.add(s);
                itm.setLore(lore);
                it.setItemMeta(itm);
                inv.setItem(i, it);
            }
            i++;
        }
        return inv;
    }

    public List<String> getValues() {
        List<String> l = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<String, String>> en : values.entrySet()) {
            StringBuilder sb = new StringBuilder("§bValeur " + en.getKey() + " §b: " + en.getKey().substring(0, 2) + "§l");
            Class<? extends Scenario> sc;
            Object o = null;
            try {
                sc = (Class<? extends Scenario>) Class.forName(en.getValue().getKey().split("\\.")[0].replace("/", "fr.neyuux.uhc.scenario.classes.").replace("@", "modes."));
                o = sc.getField(en.getValue().getKey().split("\\.")[1]).get(sc.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                e.printStackTrace();
                Bukkit.broadcastMessage(UHC.getPrefix() + "§4[§cErreur§4] §cEchec du chargement des valeurs du scénario " + this);
            }
            if (o instanceof Boolean)
                sb.append(GameConfig.getStringBoolean((boolean)o));
            else if (o instanceof Integer && en.getValue().getValue().equals("timer"))
                sb.append(UHC.getTimer((int)o));
            else if (o instanceof List)
                sb.append(o.toString().replace("[", "").replace("]", "").replace(",", en.getKey().substring(0, 2) + "§l,")).append(en.getValue().getValue().replace("[", "").replace("]", "").replace(",", en.getKey().substring(0, 2) + "§l,"));
            else
                sb.append(o.toString()).append(en.getValue().getValue());
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

    public static Scenarios getByName(String name) {
        for (Scenarios sc : Scenarios.values())
            if (sc.getName().equals(name))
                return sc;
        return null;
    }


    private static Inventory createConfigInv(String dname, int slots, List<AbstractMap.SimpleEntry<Integer, ItemsStack>> items) {
        Inventory inv;
        if (slots == 5) inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§cConfig " + dname);
        else inv = Bukkit.createInventory(null, slots, "§cConfig " + dname);

        for (AbstractMap.SimpleEntry<Integer, ItemsStack> en : items) {
            ItemsStack it = en.getValue();
            it.addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            it.addLore("", "§b>>Cliquer pour modifier");
            inv.setItem(en.getKey(), en.getValue().toItemStack());
        }
        GameConfig.setReturnArrow(inv);

        return inv;
    }

    private static AbstractMap.SimpleEntry<Integer, ItemsStack> getEntry(Integer o1, ItemsStack o2) {
        return new AbstractMap.SimpleEntry<>(o1, o2);
    }

    private static AbstractMap.SimpleEntry<Map.Entry<String, String>, Map.Entry<String, String>> getValue(String k, String v, String type, String varName) {
        Map.Entry<String, String> hm = new AbstractMap.SimpleEntry<>(v, type);
        AbstractMap.SimpleEntry<String, String> kv = new AbstractMap.SimpleEntry<>(k, varName);
        return new AbstractMap.SimpleEntry<>(kv, hm);
    }

}