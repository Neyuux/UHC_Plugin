package fr.neyuux.uhc.scenario.classes;

import fr.neyuux.uhc.UHC;
import fr.neyuux.uhc.enums.Symbols;
import fr.neyuux.uhc.scenario.Scenario;
import fr.neyuux.uhc.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionLimiter extends Scenario implements Listener {
    public PotionLimiter() {
        super(Scenarios.POTION_LIMITER, new ItemStack(Material.BREWING_STAND_ITEM));
    }

    public static boolean hasPotions = true, hasLevel2Potions = true, hasExtendedPotions = true, hasSplash = true,
        hasStrength = true, hasSpeed = true, hasNightVision = true, hasJumpBoost = true, hasFireResistance = true,
        hasWaterBreathing = true, hasHeal = true, hasPoison = true, hasRegeneration = true;

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
    public void onCraftPotion(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (e.getInventory() instanceof BrewerInventory) {
            for (int i = 0; i < 3; i++)
                if (inv.getItem(i).getType().equals(Material.POTION)) {
                    Potion pot = Potion.fromItemStack(inv.getItem(i));
                    if (!hasPotions) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions sont d�sactiv�es.");
                    if (!hasLevel2Potions && pot.getLevel() > 1) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de niveau 2 sont d�sactiv�es.");
                    if (!hasExtendedPotions && pot.hasExtendedDuration()) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions allong�es sont d�sactiv�es.");
                    if (!hasSplash && pot.isSplash()) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions jetables sont d�sactiv�es.");
                    for (PotionEffect pe : pot.getEffects())
                        if (!hasStrength && pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de force sont d�sactiv�es.");
                        else if (!hasSpeed && pe.getType().equals(PotionEffectType.SPEED)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de rapidit� sont d�sactiv�es.");
                        else if (!hasNightVision && pe.getType().equals(PotionEffectType.NIGHT_VISION)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de vision nocturne sont d�sactiv�es.");
                        else if (!hasJumpBoost && pe.getType().equals(PotionEffectType.JUMP)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de saut am�lior� sont d�sactiv�es.");
                        else if (!hasFireResistance && pe.getType().equals(PotionEffectType.FIRE_RESISTANCE)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de r�sistance au feu sont d�sactiv�es.");
                        else if (!hasWaterBreathing && pe.getType().equals(PotionEffectType.WATER_BREATHING)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de respiration aquatique sont d�sactiv�es.");
                        else if (!hasHeal && pe.getType().equals(PotionEffectType.HEAL)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de soin sont d�sactiv�es.");
                        else if (!hasPoison && pe.getType().equals(PotionEffectType.POISON)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de poison sont d�sactiv�es.");
                        else if (!hasRegeneration && pe.getType().equals(PotionEffectType.REGENERATION)) cancel(p, e, pot.toItemStack(1), getPrefix() + "�cLes potions de r�g�n�ration sont d�sactiv�es.");
                }
        }
    }



    private void cancel(Player player, InventoryOpenEvent ev, ItemStack pot, String message) {
        ev.getInventory().remove(pot);
        player.closeInventory();
        player.sendMessage(message);
        UHC.playNegativeSound(player);
    }
}
