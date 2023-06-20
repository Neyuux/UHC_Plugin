package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.events.PlayerEliminationEvent;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Random;

public class Bookception extends Scenario implements Listener {
    public Bookception() {
        super(Scenarios.BOOKCEPTION, new ItemStack(Material.ENCHANTED_BOOK));
    }

    @Override
    public void activate() {

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
    public void onKill(PlayerEliminationEvent ev) {
        if (ev.getKiller() != null) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bm = (EnchantmentStorageMeta)book.getItemMeta();
            Random r = new Random();
            Enchantment en = Enchantment.values()[r.nextInt(Enchantment.values().length)];
            bm.addStoredEnchant(en, r.nextInt(en.getMaxLevel()) + 1, true);
            bm.setDisplayName(scenario.getDisplayName());
            book.setItemMeta(bm);
            ev.getPlayerUHC().getLastLocation().getWorld().dropItem(ev.getPlayerUHC().getLastLocation(), book);
        }
    }
}