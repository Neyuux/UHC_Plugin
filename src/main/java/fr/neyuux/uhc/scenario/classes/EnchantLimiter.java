package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
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

    public static int sharpnessIronMax = 3, protectionIronMax = 3, featherfallingMax = 4, thornsMax = 3, knockbackMax = 2,
        powerMax = 3, punchMax = 2, infinityMax = 1, sharpnessDiamMax = 3, protectionDiamMax = 3;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
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
            Enchantment ench = en.getKey();
            Integer value = en.getValue();
            String itemType = ev.getItem().getType().name();
            boolean isIron = itemType.contains("IRON");
            boolean isDiamond = itemType.contains("DIAMOND");
            
            if (ench.equals(Enchantment.DAMAGE_ALL))
                if (isIron && value > sharpnessIronMax) i = sharpnessIronMax;
                else if (isDiamond && value > sharpnessDiamMax) i = sharpnessDiamMax;

            if (ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL))
                if (isIron && value > protectionIronMax) i = protectionIronMax;
                else if (isDiamond && value > protectionDiamMax) i = protectionDiamMax;

            if (ench.equals(Enchantment.PROTECTION_FALL) && value > featherfallingMax) i = featherfallingMax;
            if (ench.equals(Enchantment.THORNS) && value > thornsMax) i = thornsMax;
            if (ench.equals(Enchantment.KNOCKBACK) && value > knockbackMax) i = knockbackMax;
            if (ench.equals(Enchantment.ARROW_DAMAGE) && value > powerMax) i = powerMax;
            if (ench.equals(Enchantment.ARROW_KNOCKBACK) && value > punchMax) i = punchMax;
            if (ench.equals(Enchantment.ARROW_INFINITE) && value > infinityMax) i = infinityMax;

            if (i != -1) {
                Player player = ev.getEnchanter();

                enchantsToAdd.remove(en.getKey());

                if (i != 0) {
                    player.sendMessage(getPrefix() + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(en.getKey()) + " " + en.getValue() + " §cmais comme il dépassait le niveau maximal, il sera de niveau §6§l" + i + "§c.");
                    enchantsToAdd.put(en.getKey(), i);
                } else
                    player.sendMessage(getPrefix() + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(en.getKey()) + " " + en.getValue() + " §cmais cet enchantement est désactivé. Il a donc été supprimé");
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
                    Enchantment ench = en.getKey();
                    Integer value = en.getValue();
                    String itemType = result.getType().name();
                    boolean isIron = itemType.contains("IRON");
                    boolean isDiamond = itemType.contains("DIAMOND");

                    if (ench.equals(Enchantment.DAMAGE_ALL))
                        if (isIron && value > sharpnessIronMax) i = sharpnessIronMax;
                        else if (isDiamond && value > sharpnessDiamMax) i = sharpnessDiamMax;

                    if (ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL))
                        if (isIron && value > protectionIronMax) i = protectionIronMax;
                        else if (isDiamond && value > protectionDiamMax) i = protectionDiamMax;

                    if (ench.equals(Enchantment.PROTECTION_FALL) && value > featherfallingMax) i = featherfallingMax;
                    else if (ench.equals(Enchantment.THORNS) && value > thornsMax) i = thornsMax;
                    else if (ench.equals(Enchantment.KNOCKBACK) && value > knockbackMax) i = knockbackMax;
                    else if (ench.equals(Enchantment.ARROW_DAMAGE) && value > powerMax) i = powerMax;
                    else if (ench.equals(Enchantment.ARROW_KNOCKBACK) && value > punchMax) i = punchMax;
                    else if (ench.equals(Enchantment.ARROW_INFINITE) && value > infinityMax) i = infinityMax;

                    if (i != -1) {
                        result.removeEnchantment(ench);
                        if (i != 0) {
                            p.sendMessage(getPrefix() + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(ench) + " " + value + " §cmais comme il dépassait le niveau maximal, il sera de niveau §6§l" + i + "§c.");
                            result.addEnchantment(ench, i);
                        } else
                            p.sendMessage(getPrefix() + "§cVous étiez censé avoir §6§l" + CommandEnchant.translateEnchantName(ench) + " " + value + " §cmais cet enchantement est désactivé. Il a donc été supprimé");
                    }
                }
            }
        }
    }

}
