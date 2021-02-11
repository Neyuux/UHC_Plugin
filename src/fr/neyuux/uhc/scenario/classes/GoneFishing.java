package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GoneFishing extends Scenario implements Listener {
    public GoneFishing() {
        super(Scenarios.GONE_FISHING, new ItemStack(Material.FISHING_ROD));
    }

    @Override
    public void activate() {

    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Index.getInstance());

        for (PlayerUHC pu : Index.getInstance().getAlivePlayers()) {
            ItemsStack g = new ItemsStack(Material.FISHING_ROD, scenario.getDisplayName());
            g.addUnSafeEnchantement(Enchantment.LURE, 7);
            g.addUnSafeEnchantement(Enchantment.LUCK, 250);
            pu.getPlayer().getPlayer().getInventory().addItem(g.toItemStackWithUnbreakable());
        }
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
