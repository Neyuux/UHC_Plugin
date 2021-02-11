package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OreLimiter extends Scenario {
    public OreLimiter() {
        super(Scenarios.ORE_LIMITER, new ItemStack(Material.IRON_ORE));
    }

    public static int diamonds = 20, golds = 60, irons = 120;

    @Override
    protected void activate() {
        /*if (Scenarios.ORE_LIMITER.isActivated())
            if (up.getDiamonds() > OreLimiter.diamonds) {
                int o = new Random().nextInt(2) + 1;
                e.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItem(b.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.GOLD_INGOT, o));
                ExperienceOrb orb = b.getWorld().spawn(b.getLocation().add(0.5, 0.5, 0.5), ExperienceOrb.class);
                orb.setExperience(e.getExpToDrop());
                player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §bVous avez dépassé la limite de dimants. Vous avez reçu §6" + o + " d'or§b à place.");
            } else if (up.getDiamonds() == OreLimiter.diamonds)
                player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§b La limite de diamants a été atteinte. Désormais lorsque vous minerez du diamant, vous receverez 1 ou 2 ors.");
        if (Scenarios.ORE_LIMITER.isActivated())
                if (up.getDiamonds() > OreLimiter.golds) {
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                    player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §eVous avez dépassé la limite d'ors.");
                } else if (up.getDiamonds() == OreLimiter.golds)
                    player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§e La limite de d'ors a été atteinte.");
        if (Scenarios.ORE_LIMITER.isActivated())
                if (up.getDiamonds() > OreLimiter.irons) {
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                    player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + " §fVous avez dépassé la limite de fers.");
                } else if (up.getDiamonds() == OreLimiter.irons)
                    player.sendMessage(main.getPrefix() + Scenarios.ORE_LIMITER.getDisplayName() + "§8§l" + Symbols.DOUBLE_ARROW + "§f La limite de fers a été atteinte.");*/
    }


    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return true;
    }
}
