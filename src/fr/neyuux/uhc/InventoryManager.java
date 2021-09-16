package fr.neyuux.uhc;

import fr.neyuux.uhc.scenario.Scenarios;
import fr.neyuux.uhc.scenario.classes.Anonymous;
import fr.neyuux.uhc.util.ItemsStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    public static ItemStack[] startInventory = new ItemStack[] {};
    private static final HashMap<Integer, ItemStack> startArmor = new HashMap<>();
    private static final List<ItemStack> deathInventory = new ArrayList<>();

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static void setAllPlayersLevels(int level, float exp) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setLevel(level);
            p.setExp(exp);
        }
    }

    public static void clearAllPlayersInventories(Player... ps) {
        if (ps == null)
            for (Player p : Bukkit.getOnlinePlayers())
                clearInventory(p);
        else
            for (Player p : ps)
                clearInventory(p);
    }

    public static void clearEffects(Player p) {
        for (PotionEffect pe : p.getActivePotionEffects())
            p.removePotionEffect(pe.getType());
    }

    public static void clearAllPlayersEffects() {
        for (Player p : Bukkit.getOnlinePlayers())
            clearEffects(p);
    }

    public static void give(Player p, Integer slot, ItemStack it) {
        if (it.getType().equals(Material.ENCHANTED_BOOK)) {
            HashMap<Enchantment, Integer> map = new HashMap<>(it.getEnchantments());
            for (Enchantment en : map.keySet()) it.removeEnchantment(en);
            for (Map.Entry<Enchantment, Integer> en : map.entrySet()) {
                EnchantmentStorageMeta em = (EnchantmentStorageMeta) it.getItemMeta();
                em.addStoredEnchant(en.getKey(), en.getValue(), true);
                it.setItemMeta(em);
            }
        }

        if (p.getInventory().firstEmpty() != -1)
            if (slot == null) p.getInventory().addItem(it);
            else p.getInventory().setItem(slot, it);
        else
            p.getWorld().dropItem(p.getLocation(), it);
    }

    public static void giveWaitInventory(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SATURATION))
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        if (!player.equals(UHC.getInstance().getGameConfig().deathInvModifier) && !player.equals(UHC.getInstance().getGameConfig().starterModifier)) {
            clearInventory(player);
            player.getInventory().setItem(1, UHC.getSpecTear());
            if (((String) GameConfig.ConfigurableParams.TEAMTYPE.getValue()).startsWith("To"))
                give(player, 4, GameConfig.getChooseTeamBanner(UHC.getInstance().getPlayerUHC(player)));
            if (UHC.getInstance().getPlayerUHC(player).isHost())
                player.getInventory().setItem(6, new ItemsStack(Material.REDSTONE_COMPARATOR, "§c§lConfiguration de la partie", "§7Permet de configurer la partie", "§b>>Clique droit").toItemStack());
        }
    }

    public void giveStartInventory(Player player) {
        PlayerInventory pi = player.getInventory();
        if (startArmor.get(0) != null)pi.setHelmet(startArmor.get(0));
        if (startArmor.get(1) != null)pi.setChestplate(startArmor.get(1));
        if (startArmor.get(2) != null)pi.setLeggings(startArmor.get(2));
        if (startArmor.get(3) != null)pi.setBoots(startArmor.get(3));

        player.getInventory().setContents(startInventory);
    }

    public static void dropDeathStuff(Player player, Location loc){
        for(ItemStack item : deathInventory) if (item != null && !item.getType().equals(Material.AIR))
            player.getWorld().dropItem(loc, item);
        for (ItemStack item : player.getInventory().getContents()) if (item != null && !item.getType().equals(Material.AIR))
            player.getWorld().dropItem(loc, item);
        for (ItemStack item : player.getInventory().getArmorContents()) if (item != null && !item.getType().equals(Material.AIR))
            player.getWorld().dropItem(loc, item);

        if ((boolean)GameConfig.ConfigurableParams.HEAD.getValue()) {
            String name = player.getName();
            if (Scenarios.ANONYMOUS.isActivated()) name = Anonymous.usedName;
            player.getWorld().dropItem(loc, new ItemsStack(Material.SKULL_ITEM, (short)3).toItemStackwithSkullMeta(name));
        }
        if ((boolean)GameConfig.ConfigurableParams.GOLDEN_HEAD.getValue())
            player.getWorld().dropItem(loc, UHC.getGoldenHead(1));
    }

    public static void createChestInventory(PlayerUHC died, Chest chest, boolean explosion, int timer) {
        for (ItemStack i : died.getLastArmor().values())
            if (i != null && i.getType() != Material.AIR)
                chest.getInventory().addItem(i);

        for (ItemStack i : died.getLastInv())
            if (i != null && i.getType() != Material.AIR)
                chest.getInventory().addItem(i);

        for (ItemStack i : deathInventory)
            if (i != null && i.getType() != Material.AIR)
                chest.getInventory().addItem(i);
        if ((boolean)GameConfig.ConfigurableParams.HEAD.getValue())
            chest.getInventory().addItem(new ItemsStack(Material.SKULL_ITEM, (short)3).toItemStackwithSkullMeta(died.getPlayer().getName()));
        if ((boolean)GameConfig.ConfigurableParams.GOLDEN_HEAD.getValue())
            chest.getInventory().addItem(UHC.getGoldenHead(1));

        chest.setMetadata("nobreak", new FixedMetadataValue(UHC.getInstance(), "nobreak"));


        if(!explosion) return;

        ArmorStand ar = UHCWorld.getArmorStand(new Location(chest.getWorld(), chest.getX() + 0.5, chest.getY()+1, chest.getZ() + 0.5));
        ar.setSmall(true);
        new BukkitRunnable() {
            int time = timer;
            public void run() {
                if (time > 0)
                    ar.setCustomName("§6Explosion dans §l" + time + " §6seconde" + (time > 1 ? "s" : ""));
                else if (time == 0) {
                    ar.setCustomName("§c§lBOOM !");
                    if(chest.getBlock().getType() == Material.CHEST){
                        chest.getInventory().clear();
                        chest.update();
                    }
                    chest.getLocation().getWorld().createExplosion(chest.getLocation(), 5);
                } else {
                    ar.remove();
                    cancel();
                }
                time--;
            }
        }.runTaskTimer(UHC.getInstance(), 1, 20);
    }

    public static void giveDeathInventory(Player player) {
        for (ItemStack it : deathInventory) give(player, null, it);
    }

    public HashMap<Integer, ItemStack> getStartArmor() {
        return startArmor;
    }

    public List<ItemStack> getDeathInventory() {
        return deathInventory;
    }

    public int getStartInventorySize() {
        int size = startArmor.size();
        for (ItemStack it : startInventory) if (it != null) size++;
        return size;
    }

    public static int getDeathInventorySize() {
        return deathInventory.size();
    }

}
