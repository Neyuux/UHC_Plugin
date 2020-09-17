package fr.neyuux.uhc.scenario;

import java.util.ArrayList;
import java.util.List;

import fr.neyuux.uhc.scenario.classes.*;

public enum Scenarios {

    CUT_CLEAN(0, "CutClean", false, false, "", CutClean.class, false),
    FAST_SMELTING(1, "FastSmelting", false, false, "", FastSmelting.class, false),
    HASTEY_BOYS(2, "HasteyBoys", false, false, "", HasteyBoys.class, false),
    BLOOD_DIAMOND(3, "BloodDiamond", false, false, "", BloodDiamond.class, false),

    BAREBONES(4, "Barebones", false, false, "", BareBones.class, false),
    VEINMINER(5, "Veinminer", false, false, "", VeinMiner.class, false),
    SUPER_HEROES(6, "SuperHeroes", false, false, "", SuperHeroes.class, false),

    FIRE_LESS(7, "FireLess", false, false, "", FireLess.class, false),
    NO_FALL(8, "NoFall", false, false, "", NoFall.class, false),
    TIME_BOMB(9, "TimeBomb", false, false, "", TimeBomb.class, false),

    BOW_SWAP(10, "BowSwap", false, false, "", BowSwap.class, true),
    BOOKCEPTION(11, "Bookception", false, false, "", Bookception.class, false),
    NO_CLEANUP(12, "NoCleanUP", false, false, "", NoCleanUP.class, true),
    BLEEDING_SWEET(13, "BleedingSweets", false, false, "", BleedingSweet.class, false),

    ROD_LESS(14, "RodLess", false, false, "", RodLess.class, false),
    BOW_LESS(15, "BowLess", false, false, "", BowLess.class, false),
    NO_ANVIL(16, "NoAnvil", false, false, "", NoAnvil.class, false),
    FIRE_ENCHANT_LESS(17, "Fire Enchant Less", false, false, "", FireEnchantLess.class, false),
    GONE_FISHING(18, "GoneFishing", false, false, "", GoneFishing.class, false),

    TIMBER(19, "Timber", false, false, "", Timber.class, false),
    SKY_HIGH(20, "SkyHigh", false, false, "", SkyHigh.class, false),
    NETHERIBUS(21, "Netheribus", false, false, "", Netheribus.class, false),
    LOOT_CRATE(22, "LootCrate", false, false, "", LootCrate.class, false),

    ASSAULT_AND_BATTERY(23, "Assault & Battery", false, false, "", AssaultAndBattery.class, false),
    ANONYMOUS(24, "Anonymous", false, false, "", Anonymous.class, true),
    BEST_PVE(25, "BestPVE", false, false, "", BestPVE.class, false),
    NO_ENCHANT(26, "NoEnchant", false, false, "", NoEnchant.class, false),
    PARANOIA(27, "Paranoia", false, false, "", Paranoia.class, false),

    FAST_GETAWAY(28, "FastGetaway", false, false, "", FastGetaway.class, false),
    NO_BOOK_SHELF(29, "NoBookShelf", false, false, "", NoBookShelf.class, false),
    KILL_EFFECT(30, "KillEffect", false, false, "", KillEffect.class, false),

    DIAMOND_LESS(31, "DiamondLess", false, false, "", DiamondLess.class, false),
    VANILLA_PLUS(32, "Vanilla+", false, false, "", VanillaPlus.class, false),
    TRUE_LOVE(33, "TrueLove", false, false, "", TrueLove.class, false),

    REWARDING_LONGSHOT(34, "RewardingLongShot", false, false, "", RewardingLongShot.class, false),
    ENCHANTED_DEATH(35, "EnchantedDeath", false, false, "", EnchantedDeath.class, false),

    TEAM_INVENTORY(36, "TeamInventory", false, false, "", TeamInventory.class, false),
    NINE_SLOTS(37, "NineSlots", false, false, "", NineSlots.class, false),
    ETERNAL_NIGHT(38, "EternalNight", false, false, "", EternalNight.class, false),

    INFINITE_ENCHANTER(39, "InfiniteEnchanter", false, false, "", InfiniteEnchanter.class, false),
    MASTER_LEVEL(40, "MasterLevel", false, false, "", MasterLevel.class, false),
    GRAVE_ROBBERS(41, "GraveRobbers", false, false, "", GraveRobbers.class, false),
    FLOWER_POWER(42, "FlowerPower", false, false, "", FlowerPower.class, false),

    BLOOD_ENCHANT(43, "BloodEnchant", false, false, "", BloodEnchant.class, false),
    ALONE_TOGETHER(44, "AloneTogether", false, false, "", AloneTogether.class, false),
    TEAM_HEALTH(45, "TeamHealth", false, false, "", TeamHealth.class, false),
    RED_ARROW(46, "RedArrow", false, false, "", RedArrow.class, false),

    KINGS(47, "Kings", false, false, "", Kings.class, false),

    MOLES(48, "TaupeGun", false, true, "", Moles.class, true),
    SWITCH(49,"Switch",false,true,"",Switch.class,true),
    LG(50, "Loup-Garou", false, true,"", LoupGarou.class, true);


    private int ID;
    private String name, lore;
    private boolean isActivated, isRoleScenario, isModifiable;
    private Class<?> classe;

    Scenarios(int ID, String name, boolean isActivated, boolean isRoleScenario, String lore, Class<? extends Scenario> classe, boolean isModifiable){
        this.ID = ID;
        this.name = name;
        this.isActivated = isActivated;
        this.isRoleScenario = isRoleScenario;
        this.classe = classe;
        this.isModifiable = isModifiable;
        this.lore = lore;
    }

    public Integer getID(){
        return this.ID;
    }

    public String getName() {
        return name;
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

    public boolean isModifiable(){
        return this.isModifiable;
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

}