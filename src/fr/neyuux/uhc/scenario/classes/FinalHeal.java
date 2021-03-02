package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.commands.CommandHeal;
import fr.neyuux.uhc.enums.Gstate;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FinalHeal extends Scenario {
    public FinalHeal() {
        super(Scenarios.FINAL_HEAL, new ItemStack(Material.CARROT_ITEM));
    }

    public static int timer = 600;

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {

        final int[] timer = {FinalHeal.timer};
        new BukkitRunnable() {
            @Override
            public void run() {
                timer[0]--;
                if (timer[0] == 0) {
                    Bukkit.broadcastMessage(Index.getStaticPrefix() + scenario.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §dActivation du Scénario !");
                    CommandHeal.healAll();
                    timer[0] = FinalHeal.timer;
                    cancel();
                }
                if (!Index.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(Index.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
