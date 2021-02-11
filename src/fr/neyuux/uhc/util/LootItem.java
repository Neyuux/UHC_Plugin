package fr.neyuux.uhc.util;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootItem {
    private final ItemStack item;
    private final Interval<Integer> amounts;
    private final Double percent;

    public LootItem(ItemStack item, Double percent, Interval<Integer> amounts) {
        this.item = item;
        this.percent = percent;
        this.amounts = amounts;
    }

    public ItemStack getLootItem() {
        double ran;
        do ran = new Random().nextDouble() * (percent - 0.0) + 0.0; while (ran != 0.0);
        if (ran <= percent) {
            item.setAmount(amounts.getAsRandomInt());
            return item.clone();
        } else
            return null;
    }
}
