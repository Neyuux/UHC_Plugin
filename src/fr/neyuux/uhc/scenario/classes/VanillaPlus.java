package fr.neyuux.uhc.scenario.classes;

import com.google.common.collect.Lists;
import fr.neyuux.uhc.config.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.Interval;
import fr.neyuux.uhc.util.Loot;
import fr.neyuux.uhc.util.LootItem;
import fr.neyuux.uhc.util.VarsLoot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaPlus extends Scenario {
    public VanillaPlus() {
        super(Scenarios.VANILLA_PLUS, new ItemStack(Material.APPLE));
    }

    public static int apples = 2, flints = 2;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        VarsLoot.getBlocksLoots().put(Material.LEAVES, new Loot(0, Lists.newArrayList(
                new LootItem(new ItemStack(Material.APPLE, 1), (double) GameConfig.ConfigurableParams.APPLE.getValue(), new Interval<>(apples, apples)))));
        VarsLoot.getBlocksLoots().put(Material.LEAVES_2, new Loot(0, Lists.newArrayList(
                new LootItem(new ItemStack(Material.APPLE, 1), (double)GameConfig.ConfigurableParams.APPLE.getValue(), new Interval<>(apples, apples)))));

        double flint = (double)GameConfig.ConfigurableParams.FLINT.getValue();
        VarsLoot.getBlocksLoots().put(Material.GRAVEL, new Loot(0, Lists.newArrayList(
                new LootItem(new ItemStack(Material.FLINT, 1), flint, new Interval<>(flints, flints)),
                new LootItem(new ItemStack(Material.GRAVEL, 1), 100-flint, new Interval<>(1, 1)))));
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
