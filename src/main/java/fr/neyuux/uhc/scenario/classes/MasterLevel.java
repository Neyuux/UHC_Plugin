package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MasterLevel extends Scenario {
    public MasterLevel() {
        super(Scenarios.MASTER_LEVEL, new ItemStack(Material.EXP_BOTTLE));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        for (PlayerUHC pu : UHC.getInstance().getAlivePlayers())
            if (pu.getPlayer().isOnline()) {
                pu.getPlayer().getPlayer().setLevel(10000);
                pu.getPlayer().getPlayer().sendMessage(getPrefix() + "�aVous gagnez 10000 niveaux.");
            }
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
