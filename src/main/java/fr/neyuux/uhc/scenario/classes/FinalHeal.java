package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
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

    public static final int[] IGtimers = {0};

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        IGtimers[0] = timer;
        new BukkitRunnable() {
            @Override
            public void run() {
                IGtimers[0]--;
                if (IGtimers[0] == 0) {
                    Bukkit.broadcastMessage(getPrefix() + "§dActivation du Scénario !");
                    CommandHeal.healAll();
                    IGtimers[0] = FinalHeal.timer;
                    cancel();
                }
                if (!UHC.getInstance().isState(Gstate.PLAYING)) cancel();
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20);
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
