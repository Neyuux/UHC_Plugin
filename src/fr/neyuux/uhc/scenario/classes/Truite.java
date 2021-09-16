package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.PlayerUHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

public class Truite extends Scenario implements Listener {

    public static final HashMap<PlayerUHC, PotionEffectType> effects = new HashMap<>();

    public Truite() {
        super(Scenarios.TRUITE, new ItemStack(Material.RAW_FISH));
    }

    @Override
    protected void activate() {
    }

    @Override
    public void execute() {
        Bukkit.getServer().getPluginManager().registerEvents(this, UHC.getInstance());
        Scenario.handlers.add(this);
    }

    @Override
    public boolean checkStart() {
        return true;
    }


    @EventHandler
    public void onFishing(PlayerFishEvent ev) {
        if (ev.getCaught() == null || !(ev.getCaught() instanceof Item)) return;
        if (new Random().nextInt(100) + 1 >= 33)
            ((Item) ev.getCaught()).setItemStack(new ItemsStack(Material.RAW_FISH, "§b§lTruite enchantée", "§7Mangez-la pour recevoir", "§7un effet aléatoire !").addGlowEffect().toItemStack());
    }

    @EventHandler
    public void onInteractWithFish(PlayerInteractEvent ev) {
        if (ev.getItem() == null) return;
        if (ev.getItem().hasItemMeta() && ev.getItem().getItemMeta().hasDisplayName() && ev.getItem().getItemMeta().getDisplayName().equals("§b§lTruite enchantée"))
            if (ev.getPlayer().getFoodLevel() >= 20)
                ev.getPlayer().setFoodLevel(19);
    }

    @EventHandler
    public void onEatFish(PlayerItemConsumeEvent ev) {
        if (ev.getItem() == null) return;
        if (ev.getItem().hasItemMeta() && ev.getItem().getItemMeta().hasDisplayName() && ev.getItem().getItemMeta().getDisplayName().equals("§b§lTruite enchantée")) {
            Player p = ev.getPlayer();
            PlayerUHC pu = UHC.getInstance().getPlayerUHC(p);
            if (effects.containsKey(pu)) {
                if (effects.get(pu).equals(PotionEffectType.HARM)) p.setMaxHealth(p.getMaxHealth() + 10);
                if (p.hasPotionEffect(effects.get(pu)))
                    p.removePotionEffect(effects.get(pu));
            }
            int percent = new Random().nextInt(100) + 1;
            if (percent <= 8) setEffect(pu, PotionEffectType.INCREASE_DAMAGE, 30, 1);
            else if (percent <= 13) setEffect(pu, PotionEffectType.SPEED, 45, 2);
            else if (percent <= 18) setEffect(pu, PotionEffectType.DAMAGE_RESISTANCE, 60, 1);
            else if (percent <= 20) setEffect(pu, PotionEffectType.REGENERATION, 20, 1);
            else if (percent <= 28) setEffect(pu, PotionEffectType.HEALTH_BOOST, 60, 5);
            else if (percent <= 58) setEffect(pu, PotionEffectType.JUMP, 300, 4);
            else if (percent <= 66) setEffect(pu, PotionEffectType.FIRE_RESISTANCE, 90, 1);
            else if (percent <= 67) setEffect(pu, PotionEffectType.INVISIBILITY, 300, 1);
            else if (percent <= 75) setEffect(pu, PotionEffectType.WEAKNESS, 30, 1);
            else if (percent <= 80) setEffect(pu, PotionEffectType.SLOW, 45, 2);
            else if (percent <= 82) setEffect(pu, PotionEffectType.POISON, 10, 1);
            else if (percent <= 85) {
                p.setMaxHealth(p.getMaxHealth() - 10);
                effects.put(pu, PotionEffectType.HARM);
                pu.getPlayer().getPlayer().sendMessage(getPrefix() + "§9Vous perdez §45 coeurs permanent §9pendant §l30 §9secondes !");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!effects.containsKey(pu)) {
                            cancel();
                            return;
                        }
                        p.setMaxHealth(p.getMaxHealth() + 10);
                        effects.remove(pu);
                    }
                }.runTaskLater(UHC.getInstance(), 600L);
            }
            else if (percent <= 90) setEffect(pu, PotionEffectType.CONFUSION, 60, 1);
            else setEffect(pu, PotionEffectType.NIGHT_VISION, 600, 1);
        }
    }


    private static void setEffect(PlayerUHC pu, PotionEffectType pet, int duration, int level) {
        pu.getPlayer().getPlayer().addPotionEffect(new PotionEffect(pet, duration * 20, level - 1));
        effects.put(pu, pet);
        pu.getPlayer().getPlayer().sendMessage(UHC.getPrefix() + Scenarios.TRUITE.getDisplayName() + " §8§l" + Symbols.DOUBLE_ARROW + " §9Vous recevez l'effet §l" + UHC.translatePotionEffect(pet) + " " + level + " §9pendant §l" + duration + " §9secondes !");
    }
}
