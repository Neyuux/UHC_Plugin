package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.Index;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Truite extends Scenario {

    public static final HashMap<PlayerUHC, PotionEffectType> effects = new HashMap<>();

    public Truite() {
        super(Scenarios.TRUITE, new ItemStack(Material.RAW_FISH));
    }

    @Override
    protected void activate() {
        desactivateScenario();
        Bukkit.broadcastMessage(Index.getStaticPrefix() + "§cCe scénario n'est pas utilisable.");
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onFishing(PlayerFishEvent ev) {
        if (ev.getCaught() == null || !(ev.getCaught() instanceof Item)) return;
        if (new Random().nextInt(100) + 1 >= 33) {
            Item item = (Item)ev.getCaught();
            ItemStack it = item.getItemStack();
            it.setType(Material.RAW_FISH);
            ItemMeta itm = it.getItemMeta();
            itm.setDisplayName("§b§lTruite enchantée");
            itm.setLore(Arrays.asList("§7Mangez-la pour recevoir", "§7un effet aléatoire !"));
            it.setItemMeta(itm);
            item.setItemStack(it);
        }
    }

    @EventHandler
    public void onInteractWithFish(PlayerInteractEvent ev) {
        if (ev.getItem() == null) return;
        if (ev.getItem().hasItemMeta() && ev.getItem().getItemMeta().hasDisplayName() && ev.getItem().getItemMeta().getDisplayName().equals("§b§lTruite enchantée")) {
            if (ev.getPlayer().getFoodLevel() >= 20)
                ev.getPlayer().setFoodLevel(19);
        }
    }

    @EventHandler
    public void onEatFish(PlayerItemConsumeEvent ev) {
        if (ev.getItem() == null) return;
        if (ev.getItem().hasItemMeta() && ev.getItem().getItemMeta().hasDisplayName() && ev.getItem().getItemMeta().getDisplayName().equals("§b§lTruite enchantée")) {
            Player p = ev.getPlayer();
            PlayerUHC pu = Index.getInstance().getPlayerUHC(p);
            if (effects.containsKey(pu))
                if (p.hasPotionEffect(effects.get(pu)))
                    p.removePotionEffect(effects.get(pu));
            int percent = new Random().nextInt(100) + 1;
            //if (percent <= 8)
        }
    }


    private static void setEffect(PlayerUHC pu, PotionEffectType pet, int duration, int level) {
        pu.getPlayer().getPlayer().addPotionEffect(new PotionEffect(pet, duration * 20, level - 1));
        effects.put(pu, pet);
        pu.getPlayer().getPlayer().sendMessage(Index.getStaticPrefix() + Scenarios.TRUITE.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §9Vous recevez l'effet §l" + Index.translatePotionEffect(pet) + " " + level + " §9pendant §l" + duration + " §9secondes !");
    }
}
