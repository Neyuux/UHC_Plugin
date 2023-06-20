package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.GameConfig;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Target extends Scenario {

    public Target() {
        super(Scenarios.TARGET, new ItemStack(Material.BARRIER));
    }

    @Override
    protected void activate() {
        desactivateScenario();
        Bukkit.broadcastMessage(UHC.getPrefix() + "§cCe scénario est inutilisable.");
    }

    @Override
    public void execute() {
        ShapedRecipe recipe = new ShapedRecipe(getCompass());
        recipe.shape("IGI", "DCD", "IGI");
        recipe.setIngredient('I', Material.IRON_INGOT).setIngredient('G', Material.GOLD_INGOT).setIngredient('D', Material.DIAMOND).setIngredient('C', Material.COMPASS);
        Bukkit.getServer().addRecipe(recipe);
    }

    @Override
    public boolean checkStart() {
        return GameConfig.ConfigurableParams.TEAMTYPE.getValue().equals("FFA");
    }


    @EventHandler
    public void onCraftCompass(CraftItemEvent ev) {
        
    }


    private static ItemStack getCompass() {
        ItemsStack it = new ItemsStack(Material.COMPASS, "§cTraqueur de Target", "§7Cette boussole traque la ", "§7Target de la partie.");
        it.addGlowEffect();
        return it.toItemStack();
    }
}
