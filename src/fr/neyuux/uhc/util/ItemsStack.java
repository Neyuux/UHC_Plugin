package fr.neyuux.uhc.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemsStack {

    Material material;
    String name;
    List<String> lore = new ArrayList<>();
    int amount;
    short durabilite;

    ItemStack item;

    public ItemsStack(Material material, String name) {
        this.material = material;
        this.name = name;
        this.amount = 1;
        this.durabilite = 0;
        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, String name, String... lore) {
        this.material = material;
        this.name = name;
        this.amount = 1;
        this.durabilite = 0;

        List<String> desc = new ArrayList<>();
        for (String s : lore) {
            desc.add(s);
            this.lore = desc;
        }

        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, int amount, String name) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.durabilite = 0;
        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, int amount, String name, String... lore) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.durabilite = 0;

        List<String> desc = new ArrayList<>();
        for (String s : lore) {
            desc.add(s);
            this.lore = desc;
        }

        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, int amount, short durabilite, String name) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.durabilite = durabilite;

        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, short durabilite, String name) {
        this.material = material;
        this.name = name;
        this.amount = 1;
        this.durabilite = durabilite;

        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, short durabilite) {
        this.material = material;
        this.amount = 1;
        this.durabilite = durabilite;

        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material, short durabilite, String name, String... lore) {
        this.material = material;
        this.name = name;
        this.amount = 1;
        this.durabilite = durabilite;
        List<String> desc = new ArrayList<>();
        for (String s : lore) {
            desc.add(s);
            this.lore = desc;
        }
        this.item = new ItemStack(material);
    }

    public ItemsStack(Material material) {
        this.material = material;
        this.name = null;
        this.amount = 1;
        this.durabilite = 0;
        this.item = new ItemStack(material);
    }

    public ItemsStack(ItemStack item) {
        this.material = item.getType();
        this.amount = item.getAmount();
        this.durabilite = item.getDurability();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            this.name = item.getItemMeta().getDisplayName();
        if (item.hasItemMeta() && item.getItemMeta().hasLore())
            this.lore = item.getItemMeta().getLore();
        this.item = item;
    }

    public ItemStack toItemStack() {

        ItemStack item = this.item;

        ItemMeta itemMeta = item.getItemMeta();
        if (this.name != null)
            itemMeta.setDisplayName(this.name);
        if (this.lore != null)
            itemMeta.setLore(this.lore);
        item.setItemMeta(itemMeta);
        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        return item;
    }

    @SafeVarargs
    public final ItemStack toItemStackWithEnchant(Map.Entry<Enchantment, Integer>... enchantment) {
        ItemStack item = this.item;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        for (Map.Entry<Enchantment, Integer> en : enchantment)
            itemMeta.addEnchant(en.getKey(), en.getValue(), true);
        item.setItemMeta(itemMeta);
        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        return item;
    }

    public ItemStack toItemStackWithUnbreakable() {

        ItemStack item = this.item;

        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);

        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack toItemStackWithUnbreakableAndItemFlag(ItemFlag... itemflag) {

        ItemStack item = this.item;

        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        itemMeta.addItemFlags(itemflag);

        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack toItemStackwithItemFlag(ItemFlag... itemflag) {

        ItemStack item = this.item;

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        itemMeta.addItemFlags(itemflag);
        item.setItemMeta(itemMeta);
        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        return item;
    }

    public ItemStack toItemStackwithSkullMeta(String owner) {
        ItemStack item = this.item;
        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        itemMeta.setOwner(owner);
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack toItemStackwithMinecraftHeadsValueMeta(String value) {
        ItemStack item = this.item;
        item.setAmount(this.amount);
        item.setDurability(this.durabilite);

        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        item.setItemMeta(itemMeta);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Set<ItemFlag> getItemFlags() {
        return item.getItemMeta().getItemFlags();
    }

    public void addItemFlag(ItemFlag... itemFlag) {
        ItemMeta itm = item.getItemMeta();
        itm.addItemFlags(itemFlag);
        item.setItemMeta(itm);
    }

    public void setLore(String... lore) {
        List<String> desc = new ArrayList<>();
        for (String s : lore) {
            desc.add(s);
            this.lore = desc;
        }
    }

    public void addLore(String... lore) {
        List<String> desc = new ArrayList<>(this.lore);
        for (String s : lore) {
            desc.add(s);
            this.lore = desc;
        }
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addEnchantement(org.bukkit.enchantments.Enchantment enchant, int level) {
        this.item.addEnchantment(enchant, level);
    }

    public void addUnSafeEnchantement(org.bukkit.enchantments.Enchantment enchant, int level) {
        this.item.addUnsafeEnchantment(enchant, level);
    }

    public void removeEnchantment(org.bukkit.enchantments.Enchantment enchant) {
        this.item.removeEnchantment(enchant);
    }

    public ItemsStack addGlowEffect() {
        ItemMeta itemMeta = this.item.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);

        this.item.setItemMeta(itemMeta);

        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.item.getEnchantments();
    }

    public int getDurability() {
        return this.durabilite;
    }

    public void setDurability(short durability) {
        this.durabilite = durability;
    }
}
