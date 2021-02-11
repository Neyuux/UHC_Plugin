package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.commands.CommandEnchant;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantLimiter extends Scenario implements Listener {
    public EnchantLimiter() {
        super(Scenarios.ENCHANT_LIMITER, new ItemStack(Material.ENCHANTED_BOOK));
    }

    public static int sharpnessMax = 5, protectionMax = 4, featherfallingMax = 4, thornsMax = 3, knockbackMax = 2,
        powerMax = 5, punchMax = 2, infinityMax = 1;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());
    }

    @Override
    public boolean checkStart() {
        return true;
    }
    
    
    @EventHandler
    public void onEnchant(EnchantItemEvent ev) {
        Map<Enchantment, Integer> enchantsToAdd = ev.getEnchantsToAdd();
        for (Map.Entry<Enchantment, Integer> en : enchantsToAdd.entrySet()) {
            int i = -1;
            if (en.getKey().equals(Enchantment.DAMAGE_ALL) && en.getValue() > sharpnessMax) i = sharpnessMax;
            if (en.getKey().equals(Enchantment.PROTECTION_ENVIRONMENTAL) && en.getValue() > protectionMax) i = protectionMax;
            if (en.getKey().equals(Enchantment.PROTECTION_FALL) && en.getValue() > featherfallingMax) i = featherfallingMax;
            if (en.getKey().equals(Enchantment.THORNS) && en.getValue() > thornsMax) i = thornsMax;
            if (en.getKey().equals(Enchantment.KNOCKBACK) && en.getValue() > knockbackMax) i = knockbackMax;
            if (en.getKey().equals(Enchantment.ARROW_DAMAGE) && en.getValue() > powerMax) i = powerMax;
            if (en.getKey().equals(Enchantment.ARROW_KNOCKBACK) && en.getValue() > punchMax) i = punchMax;
            if (en.getKey().equals(Enchantment.ARROW_INFINITE) && en.getValue() > infinityMax) i = infinityMax;

            ev.getEnchanter().sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(en.getKey()) + " " + en.getValue() + " §cmais comme il dépassait le niveau maximal, il sera de niveau §6§l" + i + "§c.");
            if (i != -1) if (i != 0) {
                en.setValue(i);
            } else {
                enchantsToAdd.remove(en.getKey(), en.getValue());
            }
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();

        if (!(inv instanceof AnvilInventory)) return;

        AnvilInventory anvil = (AnvilInventory) inv;
        InventoryView view = e.getView();
        int rawSlot = e.getRawSlot();

        if (rawSlot == view.convertSlot(rawSlot) && rawSlot == 2) {
            ItemStack result = anvil.getItem(2);

            if (result != null && result.getEnchantments() != null) {
                for (Map.Entry<Enchantment, Integer> en : result.getEnchantments().entrySet()) {
                    int i = -1;
                    if (en.getKey().equals(Enchantment.DAMAGE_ALL) && en.getValue() > sharpnessMax) i = sharpnessMax;
                    if (en.getKey().equals(Enchantment.PROTECTION_ENVIRONMENTAL) && en.getValue() > protectionMax) i = protectionMax;
                    if (en.getKey().equals(Enchantment.PROTECTION_FALL) && en.getValue() > featherfallingMax) i = featherfallingMax;
                    if (en.getKey().equals(Enchantment.THORNS) && en.getValue() > thornsMax) i = thornsMax;
                    if (en.getKey().equals(Enchantment.KNOCKBACK) && en.getValue() > knockbackMax) i = knockbackMax;
                    if (en.getKey().equals(Enchantment.ARROW_DAMAGE) && en.getValue() > powerMax) i = powerMax;
                    if (en.getKey().equals(Enchantment.ARROW_KNOCKBACK) && en.getValue() > punchMax) i = punchMax;
                    if (en.getKey().equals(Enchantment.ARROW_INFINITE) && en.getValue() > infinityMax) i = infinityMax;

                    p.sendMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(en.getKey()) + " " + en.getValue() + " §cmais comme il dépassait le niveau maximal, il sera de niveau §6§l" + i + "§c.");
                    if (i != -1) {
                        result.removeEnchantment(en.getKey());
                        if (i != 0) result.addEnchantment(en.getKey(), i);
                    }
                }
            }
        }
    }

}
