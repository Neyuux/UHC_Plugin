package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CatEyes extends Scenario {
    public CatEyes() {
        super(Scenarios.CAT_EYES, new ItemStack(Material.SEA_LANTERN));
    }

    @Override
    protected void activate() {

    }

    @Override
    public void execute() {
        for (PlayerUHC pu : UHC.getInstance().getAlivePlayers())
            if (pu.getPlayer().isOnline()) pu.getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
