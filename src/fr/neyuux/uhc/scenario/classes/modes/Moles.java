package fr.neyuux.uhc.scenario.classes.modes;

import fr.neyuux.uhc.util.ItemsStack;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public class Moles extends Scenario {
    public Moles() {
        super(Scenarios.MOLES, new ItemStack(Material.DIAMOND_SWORD));
    }

    public static int timer = 20 * 60, moleTeamSize = 3, moleTeams = 2;
    public static boolean hasApocalypse = false, superMoles = false;
    public static List<Kits> activatedKits = Kits.getActivatedKits();

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return !GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    public enum Kits {

        AERIEN("§fAerien", ENDER_PEARL, Arrays.asList(new ItemStack(ENDER_PEARL, 16), new ItemsStack(ENCHANTED_BOOK).toItemStackWithEnchant(new AbstractMap.SimpleEntry<>(Enchantment.PROTECTION_FALL, 4)), new ItemStack(FEATHER, 8))),
        ALCHIMISTE("§eAlchimiste", BREWING_STAND_ITEM, Arrays.asList(getPotion(PotionEffectType.HARM, 1, 0, true, true, PotionType.INSTANT_DAMAGE), getPotion(PotionEffectType.POISON, 33, 0, true, true, PotionType.POISON), getPotion(PotionEffectType.WEAKNESS, 60, 0, true, true, PotionType.WEAKNESS), getPotion(PotionEffectType.SLOW, 60, 0, true, true, PotionType.SLOWNESS))),
        MINEUR("§7Mineur", DIAMOND_PICKAXE, Arrays.asList(new ItemsStack(DIAMOND_PICKAXE).toItemStackWithEnchant(new AbstractMap.SimpleEntry<>(Enchantment.DIG_SPEED, 3)), new ItemStack(EXP_BOTTLE, 8))),
        PYROMANE("§6Pyromane", BLAZE_POWDER, Arrays.asList(new ItemsStack(FLINT_AND_STEEL).toItemStack(), new ItemsStack(LAVA_BUCKET).toItemStack(), new ItemsStack(ENCHANTED_BOOK).toItemStackWithEnchant(new AbstractMap.SimpleEntry<>(Enchantment.ARROW_FIRE, 1), new AbstractMap.SimpleEntry<>(Enchantment.FIRE_ASPECT, 1)))),
        SUPPORT("§dSupport", GOLDEN_APPLE, Arrays.asList(getPotion(PotionEffectType.HEAL, 1, 0, true, true, PotionType.INSTANT_HEAL), getPotion(PotionEffectType.INCREASE_DAMAGE, 30, 0, true, false, PotionType.STRENGTH), getPotion(PotionEffectType.REGENERATION, 33, 0, true, true, PotionType.REGEN), getPotion(PotionEffectType.SPEED, 45, 0, true, true, PotionType.SPEED)));


        Kits(String name, Material material, List<ItemStack> kit) {
            this.name = name;
            this.kit = kit;
            this.material = material;
            this.isActivated = true;
        }

        private final String name;
        private final List<ItemStack> kit;
        private boolean isActivated;
        private final Material material;

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public List<ItemStack> getItems() {
            return kit;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean isActivated) {
            this.isActivated = isActivated;
            activatedKits = getActivatedKits();
        }


        public static List<Kits> getActivatedKits() {
            ArrayList<Kits> al = new ArrayList<>();
            for (Kits k : Kits.values())
                if (k.isActivated())
                    al.add(k);
            return al;
        }

        public static Kits getByName(String name) {
            for (Kits k : Kits.values())
                if (k.getName().equals(name))
                    return k;
            return null;
        }

        private static ItemStack getPotion(PotionEffectType pet, int duration, int amplifier, boolean particles, boolean isSplash, PotionType pt) {
            ItemStack it = new ItemStack(POTION);
            Potion pot = new Potion(pt);
            PotionMeta ptm = (PotionMeta) it.getItemMeta();
            ptm.clearCustomEffects();
            ptm.addCustomEffect(new PotionEffect(pet, duration * 20, amplifier, particles, particles), true);
            pot.setSplash(isSplash);
            it.setItemMeta(ptm);
            pot.apply(it);

            return it;
        }

    }
}
